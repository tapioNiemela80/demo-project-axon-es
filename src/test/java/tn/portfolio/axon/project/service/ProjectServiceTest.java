package tn.portfolio.axon.project.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.common.service.IdService;
import tn.portfolio.axon.project.command.AddTaskCommand;
import tn.portfolio.axon.project.command.ApproverCommandDto;
import tn.portfolio.axon.project.command.CompleteTaskCommand;
import tn.portfolio.axon.project.command.InitializeProjectCommand;
import tn.portfolio.axon.project.controller.ProjectApprover;
import tn.portfolio.axon.project.controller.ProjectInput;
import tn.portfolio.axon.project.domain.*;
import tn.portfolio.axon.project.projection.Project;
import tn.portfolio.axon.project.projection.ProjectRepository;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;
import tn.portfolio.axon.team.event.TeamTaskCompletedEvent;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private CommandGateway commandGateway;
    @Mock
    private IdService idService;
    @Mock
    private ProjectRepository projectRepository;
    private ProjectService underTest;

    @BeforeEach
    void setup(){
        underTest = new ProjectService(commandGateway, idService, projectRepository);
    }

    @Test
    void shouldInitializeProjectSuccessfully() {
        var projectId = new ProjectId(UUID.randomUUID());
        var approverId = new ApproverId(UUID.randomUUID());
        var input = new ProjectInput(
                "My Project",
                "Cool project description",
                LocalDate.of(2025, 12, 31),
                new tn.portfolio.axon.project.controller.TimeEstimation(4, 45),
                Set.of(new ProjectApprover("Alice", ProjectRole.QA, "alice@example.com"))
        );

        when(idService.newProjectId()).thenReturn(projectId);
        when(idService.newApproverId()).thenReturn(approverId);

        InitializeProjectCommand expectedCommand = new InitializeProjectCommand(
                projectId,
                "My Project",
                "Cool project description",
                LocalDate.of(2025, 12, 31),
                new TimeEstimation(4, 45),
                Set.of(new ApproverCommandDto(
                        approverId,
                        "Alice",
                        ProjectRole.QA,
                        "alice@example.com"
                ))
        );

        when(commandGateway.send(expectedCommand)).thenReturn(CompletableFuture.completedFuture(projectId));

        var result = underTest.initializeProject(input);

        assertEquals(projectId, result.join());
        verify(commandGateway).send(expectedCommand);
    }

    @Test
    void shouldAddTaskToProjectSuccessfully() {
        var projectId = new ProjectId(UUID.randomUUID());
        var taskId = new ProjectTaskId(UUID.randomUUID());
        var estimation = new tn.portfolio.axon.project.controller.TimeEstimation(3, 30);

        when(idService.newProjectTaskId()).thenReturn(taskId);

        AddTaskCommand expectedCommand = new AddTaskCommand(
                projectId,
                taskId,
                "Implement feature X",
                "Detailed task description",
                new TimeEstimation(3, 30)
        );

        when(commandGateway.send(expectedCommand)).thenReturn(CompletableFuture.completedFuture(projectId));

        var result = underTest.addTaskToProject(
                projectId,
                "Implement feature X",
                "Detailed task description",
                estimation
        );

        assertEquals(taskId, result.join());
        verify(commandGateway).send(expectedCommand);
    }

    @Test
    void shouldThrowUnknownProjectIdExceptionWhenAggregateNotFound() {
        ProjectId projectId = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());

        AggregateNotFoundException notFound = new AggregateNotFoundException(projectId.value().toString(), "project not found");
        CompletableFuture<Object> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(notFound);

        when(idService.newProjectTaskId()).thenReturn(taskId);
        when(commandGateway.send(any(AddTaskCommand.class))).thenReturn(failedFuture);

        CompletionException ex = assertThrows(CompletionException.class, () ->
                underTest.addTaskToProject(
                        projectId,
                        "Task name",
                        "Task description",
                        new tn.portfolio.axon.project.controller.TimeEstimation(0,55)
                ).join()
        );

        assertInstanceOf(UnknownProjectIdException.class, ex.getCause());
    }
    @Test
    void shouldCompleteTaskWhenProjectFound() {
        var teamTaskId = new TeamTaskId(UUID.randomUUID());
        var teamId = new TeamId(UUID.randomUUID());
        var projectTaskId = new ProjectTaskId(UUID.randomUUID());
        var actualTime = new ActualSpentTime(1, 0);
        var projectId = new ProjectId(UUID.randomUUID());
        var project = mock(Project.class);

        when(project.getId()).thenReturn(projectId);
        when(projectRepository.findProjectByTaskId(projectTaskId.value())).thenReturn(Optional.of(project));

        CompleteTaskCommand expectedCommand = new CompleteTaskCommand(projectId, projectTaskId, actualTime);

        when(commandGateway.send(expectedCommand)).thenReturn(CompletableFuture.completedFuture("done"));

        var event = new TeamTaskCompletedEvent(teamId, teamTaskId, projectTaskId, actualTime);

        underTest.completeTask(event);
        verify(commandGateway).send(expectedCommand);
    }

    @Test
    void shouldNotSendCommandIfProjectNotFoundOnComplete() {
        var teamTaskId = new TeamTaskId(UUID.randomUUID());
        var teamId = new TeamId(UUID.randomUUID());
        var projectTaskId = new ProjectTaskId(UUID.randomUUID());
        var actualTime = new ActualSpentTime(1, 0);
        var event = new TeamTaskCompletedEvent(teamId, teamTaskId, projectTaskId, actualTime);

        when(projectRepository.findProjectByTaskId(projectTaskId.value())).thenReturn(Optional.empty());

        underTest.completeTask(event);

        verify(commandGateway, never()).send(any());
    }
}