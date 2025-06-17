package tn.portfolio.axon.team.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.stereotype.Service;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.common.service.IdService;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.UnknownProjectTaskIdException;
import tn.portfolio.axon.team.command.*;
import tn.portfolio.axon.team.domain.*;
import tn.portfolio.axon.team.projection.ProjectTaskEventRepository;
import tn.portfolio.axon.team.projection.TeamRepository;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
public class TeamService {
    private final CommandGateway commandGateway;
    private final IdService idService;
    private final ProjectTaskEventRepository projectTasks;
    private final TeamRepository teams;

    public TeamService(CommandGateway commandGateway, IdService idService, ProjectTaskEventRepository projectTasks, TeamRepository teams) {
        this.commandGateway = commandGateway;
        this.idService = idService;
        this.projectTasks = projectTasks;
        this.teams = teams;
    }

    public CompletableFuture<TeamId> addTeam(String name){
        var teamId = idService.newTeamId();
        return commandGateway.send(new CreateTeamCommand(teamId, name));
    }

    public CompletableFuture<TeamMemberId> addTeamMember(TeamId teamId, String name, String profession){
        var memberId = idService.newTeamMemberId();
        return commandGateway.send(new AddTeamMemberCommand(teamId, memberId, name, profession))
                .thenApply(id -> memberId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamMemberId> removeTeamMember(TeamId teamId, TeamMemberId memberId){
        return commandGateway.send(new RemoveTeamMemberCommand(teamId, memberId))
                .thenApply(id -> memberId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamTaskId> addTaskToTeam(TeamId teamId, ProjectTaskId taskId){
        if(teams.existsByProjectTaskId(taskId.value())){
            throw new TaskAlreadyAssignedException("Task %s already assigned to some team".formatted(taskId));
        }
        var teamTaskId = idService.newTeamTaskId();
        var projectTask = projectTasks.findById(taskId.value())
                .orElseThrow(() -> new UnknownProjectTaskIdException(taskId));
        return commandGateway.send(new AddTeamTaskCommand(teamId, teamTaskId, projectTask.getProjectTaskId(), projectTask.getName(), projectTask.getDescription()))
                .thenApply(id -> teamTaskId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamTaskId> removeTask(TeamId teamId, TeamTaskId teamTaskId){
        return commandGateway.send(new RemoveTaskCommand(teamId, teamTaskId))
                .thenApply(id -> teamTaskId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamTaskId> assignTask(TeamId teamId, TeamTaskId taskId, TeamMemberId memberId){
        return commandGateway.send(new AssignTaskCommand(teamId, taskId, memberId ))
                .thenApply(id -> taskId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamTaskId> unassignTask(TeamId teamId, TeamTaskId taskId){
        return commandGateway.send(new UnassignTaskCommand(teamId, taskId))
                .thenApply(id -> taskId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamTaskId> markInProgress(TeamId teamId, TeamTaskId taskId){
        return commandGateway.send(new MarkTaskInProgressCommand(teamId, taskId))
                .thenApply(id -> taskId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    public CompletableFuture<TeamTaskId> markTaskCompleted(TeamId teamId, TeamTaskId taskId, ActualSpentTime actualSpentTime){
        return commandGateway.send(new MarkTaskCompletedCommand(teamId, taskId, actualSpentTime))
                .thenApply(id -> taskId)
                .exceptionally(throwUnknownTeamIdExceptionIfAggregateIsMissing(teamId));
    }

    private <T> Function<Throwable, T> throwUnknownTeamIdExceptionIfAggregateIsMissing(TeamId teamId) {
        return ex -> {
            if (ex.getCause() instanceof AggregateNotFoundException) {
                throw new UnknownTeamIdException(teamId);
            }
            throw new RuntimeException(ex);
        };
    }

}
