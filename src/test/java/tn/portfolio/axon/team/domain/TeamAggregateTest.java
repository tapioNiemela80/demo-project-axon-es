package tn.portfolio.axon.team.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.team.command.*;
import tn.portfolio.axon.team.event.*;

import java.util.UUID;

class TeamAggregateTest {

    private FixtureConfiguration<TeamAggregate> fixture;
    private TeamId teamId;
    private TeamMemberId memberId;
    private TeamTaskId taskId;
    private ProjectTaskId projectTaskId;
    private ActualSpentTime actualSpentTime;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(TeamAggregate.class);
        teamId = new TeamId(UUID.randomUUID());
        memberId = new TeamMemberId(UUID.randomUUID());
        taskId = new TeamTaskId(UUID.randomUUID());
        projectTaskId = new ProjectTaskId(UUID.randomUUID());
        actualSpentTime = new ActualSpentTime(2, 30);
    }

    @Test
    void createsNewTeam() {
        CreateTeamCommand command = new CreateTeamCommand(teamId, "Test Team");
        TeamCreatedEvent event = new TeamCreatedEvent(teamId, "Test Team");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectEvents(event);
    }

    @Test
    void addsTeamMember() {
        AddTeamMemberCommand command = new AddTeamMemberCommand(teamId, memberId, "John Doe", "Developer");
        TeamMemberAddedEvent event = new TeamMemberAddedEvent(teamId, memberId, "John Doe", "Developer");

        fixture.given(new TeamCreatedEvent(teamId, "Test Team"))
                .when(command)
                .expectEvents(event);
    }

    @Test
    void addsTeamTask() {
        AddTeamTaskCommand command = new AddTeamTaskCommand(teamId, taskId, projectTaskId, "Task Title", "Task Description");
        TaskAddedToTeamEvent event = new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description");

        fixture.given(new TeamCreatedEvent(teamId, "Test Team"))
                .when(command)
                .expectEvents(event);
    }

    @Test
    void marksTaskInProgress() {
        TaskMarkedInProgressEvent event = new TaskMarkedInProgressEvent(teamId, taskId);

        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description"),
                        new TeamTaskAssignedEvent(teamId, taskId, memberId)
                )
                .when(new MarkTaskInProgressCommand(teamId, taskId))
                .expectEvents(event);
    }

    @Test
    void markTaskInProgressFailsBecauseTaskIsNotAssigned() {
        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description")
                )
                .when(new MarkTaskInProgressCommand(teamId, taskId))
                .expectException(TaskTransitionNotAllowedException.class);
    }

    @Test
    void assignsTask() {
        TeamTaskAssignedEvent event = new TeamTaskAssignedEvent(teamId, taskId, memberId);

        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TeamMemberAddedEvent(teamId, memberId, "John Doe", "Developer"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description")
                )
                .when(new AssignTaskCommand(teamId, taskId, memberId))
                .expectEvents(event);
    }

    @Test
    void cannotAssignTaskBecauseUnknownMember() {
        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description")
                )
                .when(new AssignTaskCommand(teamId, taskId, memberId))
                .expectException(UnknownTeamMemberIdException.class);
    }

    @Test
    void unassignsTask() {
        TaskUnassignedEvent event = new TaskUnassignedEvent(teamId, taskId);

        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description"),
                        new TeamTaskAssignedEvent(teamId, taskId, memberId)
                )
                .when(new UnassignTaskCommand(teamId, taskId))
                .expectEvents(event);
    }

    @Test
    void unassignTaskFailsBecauseNotAssigned() {
        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description")
                )
                .when(new UnassignTaskCommand(teamId, taskId))
                .expectException(TaskTransitionNotAllowedException.class)
                .expectExceptionMessage("Task is not assigned");
    }

    @Test
    void marksTaskCompleted() {
        TeamTaskCompletedEvent event = new TeamTaskCompletedEvent(teamId, taskId, projectTaskId, actualSpentTime);

        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description"),
                        new TeamTaskAssignedEvent(teamId, taskId, memberId),
                        new TaskMarkedInProgressEvent(teamId, taskId)
                )
                .when(new MarkTaskCompletedCommand(teamId, taskId, actualSpentTime))
                .expectEvents(event);
    }

    @Test
    void markTaskCompletedFailsWhenNotInProgress() {
        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description")
                )
                .when(new MarkTaskCompletedCommand(teamId, taskId, actualSpentTime))
                .expectException(TaskTransitionNotAllowedException.class)
                .expectExceptionMessage("task not in progress");
    }

    @Test
    void removesTask() {
        TaskRemovedFromTeamEvent event = new TaskRemovedFromTeamEvent(teamId, taskId);

        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description")
                )
                .when(new RemoveTaskCommand(teamId, taskId))
                .expectEvents(event);
    }

    @Test
    void removeTaskFailsWhenTaskIsAssigned() {
        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description"),
                        new TeamTaskAssignedEvent(teamId, taskId, memberId)
                )
                .when(new RemoveTaskCommand(teamId, taskId))
                .expectException(TaskCannotBeDeletedException.class);
    }

    @Test
    void removesTeamMember() {
        TeamMemberRemovedEvent event = new TeamMemberRemovedEvent(teamId, memberId);

        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TeamMemberAddedEvent(teamId, memberId, "John Doe", "Developer")
                )
                .when(new RemoveTeamMemberCommand(teamId, memberId))
                .expectEvents(event);
    }

    @Test
    void removeTeamMemberFailsWhenMemberNotFound() {
        fixture.given(new TeamCreatedEvent(teamId, "Test Team"))
                .when(new RemoveTeamMemberCommand(teamId, memberId))
                .expectException(UnknownTeamMemberIdException.class);
    }

    @Test
    void removeTeamMemberFailsWhenMemberHasAssignedTasks() {
        fixture.given(
                        new TeamCreatedEvent(teamId, "Test Team"),
                        new TeamMemberAddedEvent(teamId, memberId, "John Doe", "Developer"),
                        new TaskAddedToTeamEvent(teamId, taskId, projectTaskId, "Task Title", "Task Description"),
                        new TeamTaskAssignedEvent(teamId, taskId, memberId)
                )
                .when(new RemoveTeamMemberCommand(teamId, memberId))
                .expectException(TeamMemberHasAssignedTasksException.class);
    }
}