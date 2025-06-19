package tn.portfolio.axon.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.portfolio.axon.approval.service.ApprovalService;
import tn.portfolio.axon.approval.view.ApprovalView;
import tn.portfolio.axon.approval.view.ApprovalViewService;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.service.ProjectService;
import tn.portfolio.axon.project.view.ProjectView;
import tn.portfolio.axon.project.view.ProjectViewService;
import tn.portfolio.axon.project.view.ProjectsView;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ApprovalService approvalService;
    private final ProjectViewService projectViewService;
    private final ApprovalViewService approvalViewService;

    public ProjectController(ProjectService projectService, ApprovalService approvalService, ProjectViewService projectViewService, ApprovalViewService approvalViewService) {
        this.projectService = projectService;
        this.approvalService = approvalService;
        this.projectViewService = projectViewService;
        this.approvalViewService = approvalViewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<Void>> createProject(@RequestBody ProjectInput input) {
        return projectService.initializeProject(input)
                .thenApply(id -> id.value())
                .thenApply(id -> uri("/projects/" + id))
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
                .thenApply(taskId -> uri("/projects/" + projectId + "/tasks/" + taskId))
                .thenApply(this::path);
    }

    @PostMapping("/{projectId}/approvals/{approverId}") //in reality we would have authentication of course
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<Void>> handleApproval(@PathVariable UUID projectId, @PathVariable UUID approverId, @RequestBody ApprovalInput request) {
        if (request.approved()) {
            return approvalService.approve(new ProjectId(projectId), new ApproverId(approverId))
                    .thenApply(id -> ResponseEntity.noContent().build());
        } else {
            return approvalService.reject(new ProjectId(projectId), new ApproverId(approverId), request.reason())
                    .thenApply(id -> ResponseEntity.noContent().build());
        }
    }

    @GetMapping("/{projectId}/approvals")
    public List<ApprovalView> findApprovalStatus(@PathVariable UUID projectId){
        return approvalViewService.findApprovalStatusOfProject(projectId);
    }

    @GetMapping
    public List<ProjectsView> findAll(){
        return projectViewService.findAll();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectView> findOne(@PathVariable UUID projectId){
        return projectViewService.findOne(projectId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Void> path(URI uri) {
        return ResponseEntity.created(uri).build();
    }

    private URI uri(String value) {
        return URI.create(value);
    }
}