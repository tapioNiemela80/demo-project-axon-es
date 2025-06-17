package tn.portfolio.axon.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.service.ProjectService;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<Void>> createProject(@RequestBody ProjectInput input) {
        return projectService.initializeProject(input)
                .thenApply(id -> id.value())
                .thenApply(id -> uri("projects/"+id))
                .thenApply(this::path);
    }

    @PostMapping("/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<Void>> addTask(@PathVariable UUID projectId, @RequestBody AddTaskRequest request) {
        return projectService.addTaskToProject(
                        new ProjectId(projectId),
                        request.name(),
                        request.description(),
                        request.estimation()
                )
                .thenApply(id -> id.value())
                .thenApply(taskId -> uri("projects/"+projectId+"/tasks/"+taskId))
                .thenApply(this::path);
    }
    private ResponseEntity<Void> path(URI uri){
        return ResponseEntity.created(uri).build();
    }

    private URI uri(String value){
        return URI.create(value);
    }
}