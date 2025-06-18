package tn.portfolio.axon.project.projection;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.portfolio.axon.common.service.DateService;
import tn.portfolio.axon.project.event.*;

import java.time.Instant;

@Component
@ProcessingGroup("project-projection")
public class ProjectProjection {
    private final ProjectRepository projects;
    private final DateService dateService;

    public ProjectProjection(ProjectRepository projects, DateService dateService) {
        this.projects = projects;
        this.dateService = dateService;
    }

    @ResetHandler
    public void reset() {
        projects.deleteAllInBatch();
    }

    @EventHandler
    public void on(ProjectInitializedEvent event, @Timestamp Instant when) {
        Project project = new Project(
                event.projectId(),
                event.name(),
                event.description(),
                dateService.toLocalDateTime(when),
                event.estimatedEndDate(),
                event.estimation());
        projects.save(project);
    }

    @EventHandler
    @Transactional
    public void on(TaskAddedToProjectEvent event) {
        projects.findByIdWithTasks(event.projectId().value())
                .ifPresent(project -> project.addTask(event.taskId(), event.name(), event.description(), event.estimation()));
    }

    @EventHandler
    @Transactional
    public void on(ProjectTaskCompletedEvent event) {
        projects.findByIdWithTasks(event.projectId().value())
                .ifPresent(project -> project.markTaskCompleted(event.taskId(), event.actualSpentTime()));
    }

    @EventHandler
    @Transactional
    public void on(ProjectCompletedEvent event) {
        projects.findByIdWithTasks(event.projectId().value())
                .ifPresent(project -> project.markCompleted());
    }

    @EventHandler
    @Transactional
    public void on(ProjectWasApprovedEvent event) {
        projects.findByIdWithTasks(event.projectId().value())
                .ifPresent(project -> project.markApproved());
    }

    @EventHandler
    @Transactional
    public void on(ProjectWasRejectedEvent event) {
        projects.findByIdWithTasks(event.projectId().value())
                .ifPresent(project -> project.markRejected());
    }
}
