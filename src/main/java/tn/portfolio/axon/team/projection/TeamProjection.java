package tn.portfolio.axon.team.projection;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.portfolio.axon.project.event.TaskAddedToProjectEvent;
import tn.portfolio.axon.team.event.*;

@Component
@ProcessingGroup("team-projection")
public class TeamProjection {
    private final TeamRepository teams;
    private final ProjectTaskEventRepository projectTasks;

    public TeamProjection(TeamRepository teams, ProjectTaskEventRepository projectTasks) {
        this.teams = teams;
        this.projectTasks = projectTasks;
    }

    @ResetHandler
    @Transactional
    public void reset() {
        teams.deleteAllInBatch();
        projectTasks.deleteAllInBatch();
    }

    @EventHandler
    public void on(TaskAddedToProjectEvent event) {
        var id = event.taskId().value();
        var projectId = event.projectId().value();
        var estimation = event.estimation();
        projectTasks.save(new ProjectTaskEvent(id, projectId, event.name(), event.description(), estimation.getHours(), estimation.getMinutes()));
    }

    @EventHandler
    public void on(TeamCreatedEvent event) {
        teams.save(new Team(event.teamId(), event.name()));
    }

    @EventHandler
    @Transactional
    public void on(TaskAddedToTeamEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.addTask(event.teamTaskId(), event.title(), event.projectTaskId().value(), event.description()));
    }

    @EventHandler
    @Transactional
    public void on(TeamTaskAssignedEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.assignTask(event.taskId(), event.memberId()));
    }

    @EventHandler
    @Transactional
    public void on(TaskUnassignedEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.markUnassigned(event.taskId()));
    }

    @EventHandler
    @Transactional
    public void on(TaskCompletedEvent event) {
        var actualTimeSpent = event.actualTimeSpent();
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.markCompleted(event.teamTaskId(), actualTimeSpent));
    }

    @EventHandler
    @Transactional
    public void on(TaskMarkedInProgressEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.markTaskInProgress(event.taskId()));
    }

    @EventHandler
    @Transactional
    public void on(TaskRemovedFromTeamEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.removeTask(event.taskId()));
    }

    @EventHandler
    @Transactional
    public void on(TeamMemberAddedEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.addMember(event.memberId(), event.name(), event.profession()));
    }

    @EventHandler
    @Transactional
    public void on(TeamMemberRemovedEvent event) {
        teams.findByIdWithMembersAndTasks(event.teamId().value())
                .ifPresent(team -> team.removeMember(event.memberId()));
    }
}
