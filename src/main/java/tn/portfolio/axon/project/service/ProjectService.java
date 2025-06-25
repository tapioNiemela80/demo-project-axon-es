package tn.portfolio.axon.project.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.common.service.IdService;
import tn.portfolio.axon.project.command.AddTaskCommand;
import tn.portfolio.axon.project.command.ApproverCommandDto;
import tn.portfolio.axon.project.command.CompleteTaskCommand;
import tn.portfolio.axon.project.command.InitializeProjectCommand;
import tn.portfolio.axon.project.controller.ProjectInput;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;
import tn.portfolio.axon.project.domain.UnknownProjectIdException;
import tn.portfolio.axon.project.projection.Project;
import tn.portfolio.axon.project.projection.ProjectRepository;
import tn.portfolio.axon.team.event.TeamTaskCompletedEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final CommandGateway commandGateway;
    private final IdService idService;
    private final ProjectRepository projects;

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    public ProjectService(CommandGateway commandGateway, IdService idService, ProjectRepository projects) {
        this.commandGateway = commandGateway;
        this.idService = idService;
        this.projects = projects;
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

    @EventHandler
    public void completeTask(TeamTaskCompletedEvent event) {
        projects.findProjectByTaskId(event.projectTaskId().value())
                .ifPresentOrElse(project -> completeTask(project, event), () -> {
                    log.warn("couldn't find project for task %s".formatted(event.projectTaskId()));
                });
    }

    private CompletableFuture<Object> completeTask(Project project, TeamTaskCompletedEvent event) {
        return commandGateway.send(new CompleteTaskCommand(project.getId(), event.projectTaskId(), event.actualTimeSpent()));
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