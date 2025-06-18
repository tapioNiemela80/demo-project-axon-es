package tn.portfolio.axon.project.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.command.*;
import tn.portfolio.axon.project.event.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class ProjectAggregateTest {

    private AggregateTestFixture<ProjectAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(ProjectAggregate.class);
    }

    @Test
    void shouldInitializeProjectAndPlaceApprovers() {
        var projectId = new ProjectId(UUID.randomUUID());
        var estimatedEndDate = LocalDate.of(2025, 12, 31);
        var estimation = new TimeEstimation(10, 30);
        var approverId = new ApproverId(UUID.randomUUID());
        var approver = new ApproverCommandDto(approverId, "John Doe", ProjectRole.PROJECT_MANAGER, "john@example.com");

        var command = new InitializeProjectCommand(
                projectId,
                "New Project",
                "A test project",
                estimatedEndDate,
                estimation,
                Set.of(approver)
        );

        fixture.givenNoPriorActivity()
                .when(command)
                .expectEvents(
                        new ProjectInitializedEvent(projectId, "New Project", "A test project", estimatedEndDate, estimation),
                        new ProjectApproverPlacedEvent(projectId, approverId, "John Doe", ProjectRole.PROJECT_MANAGER, "john@example.com")
                );
    }

    @Test
    void shouldAddTaskWhenUnderEstimationLimit() {
        var projectId = new ProjectId(UUID.randomUUID());
        var taskId = new ProjectTaskId(UUID.randomUUID());
        var estimation = new TimeEstimation(5, 0);
        var initEvent = new ProjectInitializedEvent(projectId, "Test", "Desc", LocalDate.now().plusDays(10), new TimeEstimation(10, 0));

        var command = new AddTaskCommand(projectId, taskId, "Task", "Task desc", estimation);

        fixture.given(initEvent)
                .when(command)
                .expectEvents(new TaskAddedToProjectEvent(projectId, taskId, "Task", "Task desc", estimation));
    }

    @Test
    void shouldRejectTaskAdditionIfEstimationWouldBeExceeded() {
        var projectId = new ProjectId(UUID.randomUUID());
        var taskId = new ProjectTaskId(UUID.randomUUID());
        var initEvent = new ProjectInitializedEvent(projectId, "Test", "Desc", LocalDate.now().plusDays(10), new TimeEstimation(1, 0));
        var existingTask = new TaskAddedToProjectEvent(projectId, new ProjectTaskId(UUID.randomUUID()), "Old", "Old desc", new TimeEstimation(1, 0));
        var newCommand = new AddTaskCommand(projectId, taskId, "New", "New desc", new TimeEstimation(0, 30));

        fixture.given(initEvent, existingTask)
                .when(newCommand)
                .expectException(ProjectTimeEstimationWouldBeExceededException.class);
    }

    @Test
    void projectShouldNotBeMarkedCompleteWhenOnlySomeTasksAreCompleted() {
        var projectId = new ProjectId(UUID.randomUUID());
        var task1 = new ProjectTaskId(UUID.randomUUID());
        var task2 = new ProjectTaskId(UUID.randomUUID());
        var task3 = new ProjectTaskId(UUID.randomUUID());
        var estimation = new TimeEstimation(2, 0);
        var estimatedEndDate = LocalDate.of(2025, 12, 31);

        var initEvt = new ProjectInitializedEvent(
                projectId,
                "Test Project",
                "Only some tasks complete",
                estimatedEndDate,
                new TimeEstimation(6, 0)
        );

        var task1Added = new TaskAddedToProjectEvent(projectId, task1, "Task 1", "First", estimation);
        var task2Added = new TaskAddedToProjectEvent(projectId, task2, "Task 2", "Second", estimation);
        var task3Added = new TaskAddedToProjectEvent(projectId, task3, "Task 3", "Third", estimation);

        var task1Completed = new ProjectTaskCompletedEvent(projectId, task1, new ActualSpentTime(2, 0));
        var task2Completed = new ProjectTaskCompletedEvent(projectId, task2, new ActualSpentTime(2, 0));

        fixture.given(initEvt, task1Added, task2Added, task3Added, task1Completed)
                .when(new CompleteTaskCommand(projectId, task2, new ActualSpentTime(2, 0)))
                .expectSuccessfulHandlerExecution()
                .expectEvents(task2Completed)
                .expectState(aggregate -> aggregate.isInState(ProjectStatus.PLANNED));
    }

    @Test
    void shouldCompleteTaskAndEmitProjectCompletedEventIfAllTasksComplete() {
        var projectId = new ProjectId(UUID.randomUUID());
        var taskId = new ProjectTaskId(UUID.randomUUID());
        var estimation = new TimeEstimation(1, 0);
        var actualSpent = new ActualSpentTime(1, 0);

        var init = new ProjectInitializedEvent(projectId, "P", "D", LocalDate.now().plusDays(2), estimation);
        var taskAdded = new TaskAddedToProjectEvent(projectId, taskId, "T", "T desc", estimation);
        var command = new CompleteTaskCommand(projectId, taskId, actualSpent);

        fixture.given(init, taskAdded)
                .when(command)
                .expectEvents(
                        new ProjectTaskCompletedEvent(projectId, taskId, actualSpent),
                        new ProjectCompletedEvent(projectId, "P")
                );
    }

    @Test
    void shouldThrowWhenCompletingUnknownTask() {
        var projectId = new ProjectId(UUID.randomUUID());
        var unknownTaskId = new ProjectTaskId(UUID.randomUUID());
        var estimation = new TimeEstimation(2, 0);

        var init = new ProjectInitializedEvent(projectId, "X", "Y", LocalDate.now(), estimation);
        var command = new CompleteTaskCommand(projectId, unknownTaskId, new ActualSpentTime(1, 0));

        fixture.given(init)
                .when(command)
                .expectException(UnknownProjectTaskIdException.class);
    }

    @Test
    void shouldApproveProjectOnlyIfCompleted() {
        var projectId = new ProjectId(UUID.randomUUID());
        var init = new ProjectInitializedEvent(projectId, "Z", "Z", LocalDate.now(), new TimeEstimation(1, 0));
        var complete = new ProjectCompletedEvent(projectId, "Z");

        var command = new MarkProjectApprovedCommand(projectId);

        fixture.given(init, complete)
                .when(command)
                .expectEvents(new ProjectWasApprovedEvent(projectId));
    }

    @Test
    void shouldRejectProjectOnlyIfCompleted() {
        var projectId = new ProjectId(UUID.randomUUID());
        var init = new ProjectInitializedEvent(projectId, "Z", "Z", LocalDate.now(), new TimeEstimation(1, 0));
        var complete = new ProjectCompletedEvent(projectId, "Z");

        var command = new MarkProjectRejectedCommand(projectId);

        fixture.given(init, complete)
                .when(command)
                .expectEvents(new ProjectWasRejectedEvent(projectId));
    }

    @Test
    void shouldThrowWhenApprovingUnfinishedProject() {
        var projectId = new ProjectId(UUID.randomUUID());
        var init = new ProjectInitializedEvent(projectId, "Z", "Z", LocalDate.now(), new TimeEstimation(1, 0));

        var command = new MarkProjectApprovedCommand(projectId);

        fixture.given(init)
                .when(command)
                .expectException(ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException.class);
    }
}