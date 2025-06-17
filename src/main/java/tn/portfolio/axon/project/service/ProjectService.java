package tn.portfolio.axon.project.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.stereotype.Service;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.common.service.IdService;
import tn.portfolio.axon.project.command.AddTaskCommand;
import tn.portfolio.axon.project.command.ApproverCommandDto;
import tn.portfolio.axon.project.command.InitializeProjectCommand;
import tn.portfolio.axon.project.controller.ProjectInput;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;
import tn.portfolio.axon.project.domain.UnknownProjectIdException;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.UnknownTeamIdException;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final CommandGateway commandGateway;
    private final IdService idService;
    public ProjectService(CommandGateway commandGateway, IdService idService) {
        this.commandGateway = commandGateway;
        this.idService = idService;
    }

    public CompletableFuture<ProjectId> initializeProject(ProjectInput input) {
        var projectId = idService.newProjectId();

        Set<ApproverCommandDto> approvers = input.projectApprovers().stream()
                .map(a -> new ApproverCommandDto(
                        idService.newApproverId(),
                        a.name(),
                        a.role(),
                        a.email()
                ))
                .collect(Collectors.toSet());

        // AggregateNotFoundException
        TimeEstimation estimation = new TimeEstimation(input.estimation().hours(), input.estimation().minutes());

        InitializeProjectCommand cmd = new InitializeProjectCommand(
                projectId,
                input.name(),
                input.description(),
                input.estimatedEndDate(),
                estimation,
                approvers
        );

        return commandGateway.send(cmd);
    }

    public CompletableFuture<ProjectTaskId> addTaskToProject(ProjectId projectId, String name, String description, tn.portfolio.axon.project.controller.TimeEstimation estimation) {
        var taskId = idService.newProjectTaskId();
        return commandGateway.send(addTaskCommand(projectId, taskId, name, description, new TimeEstimation(estimation.hours(), estimation.minutes())))
                .thenApply(aggregateId -> taskId)
                .exceptionally(throwUnknownProjectIdExceptionIfAggregateIsMissing(projectId));
    }

    private AddTaskCommand addTaskCommand(ProjectId projectId, ProjectTaskId taskId, String name, String description, TimeEstimation timeEstimation) {
        return new AddTaskCommand(projectId, taskId, name, description, timeEstimation);
    }

    private <T> Function<Throwable, T> throwUnknownProjectIdExceptionIfAggregateIsMissing(ProjectId projectId) {
        return ex -> {
            if (ex.getCause() instanceof AggregateNotFoundException) {
                throw new UnknownProjectIdException(projectId);
            }
            throw new RuntimeException(ex);
        };
    }
}