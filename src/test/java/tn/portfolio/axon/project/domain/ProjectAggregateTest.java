package tn.portfolio.axon.project.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.command.*;
import tn.portfolio.axon.project.event.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

class ProjectAggregateTest {

    private FixtureConfiguration<ProjectAggregate> fixture;
    private ProjectId projectId;
    private ProjectTaskId taskId;
    private ApproverId approverId;
    private TimeEstimation estimation;
    private ActualSpentTime actualSpentTime;
    private LocalDate estimatedEndDate;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(ProjectAggregate.class);
        projectId = new ProjectId(UUID.randomUUID());
        taskId = new ProjectTaskId(UUID.randomUUID());
        approverId = new ApproverId(UUID.randomUUID());
        estimation = new TimeEstimation(10, 30); // 10h 30min
        actualSpentTime = new ActualSpentTime(2, 15); // 2h 15min
        estimatedEndDate = LocalDate.of(2025, 12, 31);
    }

    @Test
    void initializesNewProject() {
        ApproverCommandDto approver = new ApproverCommandDto(approverId, "John Doe", ProjectRole.QA, "john@example.com");
        InitializeProjectCommand command = new InitializeProjectCommand(
                projectId, "Test Project", "Description", estimatedEndDate, estimation, Set.of(approver)
        );
        ProjectInitializedEvent initEvent = new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation);
        ProjectApproverPlacedEvent approverEvent = new ProjectApproverPlacedEvent(projectId, approverId, "John Doe", ProjectRole.QA, "john@example.com");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectEvents(initEvent, approverEvent);
    }

    @Test
    void addsTaskToProject() {
        TaskAddedToProjectEvent event = new TaskAddedToProjectEvent(projectId, taskId, "Task Name", "Task Description", estimation);

        fixture.given(new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, new TimeEstimation(20, 0)))
                .when(new AddTaskCommand(projectId, taskId, "Task Name", "Task Description", estimation))
                .expectEvents(event);
    }

    @Test
    void taskAddingFailsBecauseWouldExceedOriginalTimeEstimate() {
        TimeEstimation largeEstimation = new TimeEstimation(1, 1);

        fixture.given(new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, new TimeEstimation(1, 0)))
                .when(new AddTaskCommand(projectId, taskId, "Task Name", "Task Description", largeEstimation))
                .expectException(ProjectTimeEstimationWouldBeExceededException.class);
    }

    @Test
    void taskAddingFailsBecauseProjectNeedsToBeInPlannedState() {
        fixture.given(
                        new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation),
                        new TaskAddedToProjectEvent(projectId, taskId, "Task Name", "Task Description", estimation),
                        new ProjectTaskCompletedEvent(projectId, taskId, actualSpentTime),
                        new ProjectCompletedEvent(projectId, "Test Project")
                )
                .when(new AddTaskCommand(projectId, new ProjectTaskId(UUID.randomUUID()), "New Task", "Description", estimation))
                .expectException(ProjectAlreadyCompletedException.class);
    }

    @Test
    void emitsTaskCompletedAndProjectCompletedEventsWhenAllTasksCompleted() {
        ProjectTaskCompletedEvent taskCompletedEvent = new ProjectTaskCompletedEvent(projectId, taskId, actualSpentTime);
        ProjectCompletedEvent projectCompletedEvent = new ProjectCompletedEvent(projectId, "Test Project");

        fixture.given(
                        new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation),
                        new TaskAddedToProjectEvent(projectId, taskId, "Task Name", "Task Description", estimation)
                )
                .when(new CompleteTaskCommand(projectId, taskId, actualSpentTime))
                .expectEvents(taskCompletedEvent, projectCompletedEvent)
                .expectState(aggregate -> aggregate.isInState(ProjectStatus.COMPLETED));
    }

    @Test
    void emitsTaskCompletedButNotProjectCompletedEventsWhenOnlySomeTasksCompleted() {
        var taskId1 = new ProjectTaskId(UUID.randomUUID());
        var taskId2 = new ProjectTaskId(UUID.randomUUID());
        ProjectInitializedEvent initEvent = new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, new TimeEstimation(10, 0));
        TaskAddedToProjectEvent task1Event = new TaskAddedToProjectEvent(projectId, taskId1, "Task 1", "Description 1", estimation);
        TaskAddedToProjectEvent task2Event = new TaskAddedToProjectEvent(projectId, taskId2, "Task 2", "Description 2", estimation);
        ProjectTaskCompletedEvent taskCompletedEvent = new ProjectTaskCompletedEvent(projectId, taskId1, actualSpentTime);

        fixture.given(
                        initEvent,
                        task1Event,
                        task2Event
                )
                .when(new CompleteTaskCommand(projectId, taskId1, actualSpentTime))
                .expectEvents(taskCompletedEvent)
                .expectState(aggregate -> aggregate.isInState(ProjectStatus.PLANNED));
    }

    @Test
    void completeTaskFailsWhenUnknownTaskIdGiven() {
        fixture.given(new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation))
                .when(new CompleteTaskCommand(projectId, taskId, actualSpentTime))
                .expectException(UnknownProjectTaskIdException.class);
    }

    @Test
    void completeTaskFailsWhenProjectIsNotInPlannedState() {
        fixture.given(
                        new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation),
                        new TaskAddedToProjectEvent(projectId, taskId, "Task Name", "Task Description", estimation),
                        new ProjectTaskCompletedEvent(projectId, taskId, actualSpentTime),
                        new ProjectCompletedEvent(projectId, "Test Project")
                )
                .when(new CompleteTaskCommand(projectId, taskId, actualSpentTime))
                .expectException(ProjectAlreadyCompletedException.class);
    }

    @Test
    void emitsProjectWasApprovedEventWhenApproved() {
        ProjectWasApprovedEvent event = new ProjectWasApprovedEvent(projectId);

        fixture.given(
                        new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation),
                        new TaskAddedToProjectEvent(projectId, taskId, "Task Name", "Task Description", estimation),
                        new ProjectTaskCompletedEvent(projectId, taskId, actualSpentTime),
                        new ProjectCompletedEvent(projectId, "Test Project")
                )
                .when(new MarkProjectApprovedCommand(projectId))
                .expectEvents(event);
    }

    @Test
    void approvalFailsWhenNotInCompletedState() {
        fixture.given(new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation))
                .when(new MarkProjectApprovedCommand(projectId))
                .expectException(ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException.class);
    }

    @Test
    void emitsProjectWasRejectedEventWhenRejected() {
        ProjectWasRejectedEvent event = new ProjectWasRejectedEvent(projectId);

        fixture.given(
                        new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation),
                        new TaskAddedToProjectEvent(projectId, taskId, "Task Name", "Task Description", estimation),
                        new ProjectTaskCompletedEvent(projectId, taskId, actualSpentTime),
                        new ProjectCompletedEvent(projectId, "Test Project")
                )
                .when(new MarkProjectRejectedCommand(projectId))
                .expectEvents(event);
    }

    @Test
    void rejectFailsWhenNotInCompletedState() {
        fixture.given(new ProjectInitializedEvent(projectId, "Test Project", "Description", estimatedEndDate, estimation))
                .when(new MarkProjectRejectedCommand(projectId))
                .expectException(ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException.class);
    }
}