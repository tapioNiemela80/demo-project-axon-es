package tn.portfolio.axon.team.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;
import tn.portfolio.axon.team.domain.TeamTaskId;
import tn.portfolio.axon.team.service.TeamService;
import tn.portfolio.axon.team.view.TeamView;
import tn.portfolio.axon.team.view.TeamViewService;
import tn.portfolio.axon.team.view.TeamsView;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;
    private final TeamViewService teamViewService;

    public TeamController(TeamService teamService, TeamViewService teamViewService) {
        this.teamService = teamService;
        this.teamViewService = teamViewService;
    }

    private ResponseEntity<Void> path(URI uri) {
        return ResponseEntity.created(uri).build();
    }

    private ResponseEntity<Void> noContent(Object param) {
        return ResponseEntity.noContent().build();
    }

    private URI uri(String value) {
        return URI.create(value);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<Void>> create(@RequestBody TeamInput teamInput) {
        return teamService.addTeam(teamInput.name())
                .thenApply(TeamId::value)
                .thenApply(id -> uri("/teams/" + id))
                .thenApply(this::path);
    }

    @PostMapping("/{teamId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<ResponseEntity<Void>> addMember(@PathVariable UUID teamId, @RequestBody MemberInput memberInput) {
        return teamService.addTeamMember(new TeamId(teamId), memberInput.name(), memberInput.profession())
                .thenApply(TeamMemberId::value)
                .thenApply(id -> uri("/teams/" + teamId + "/members/" + id))
                .thenApply(this::path);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{teamId}/tasks/by-project-id/{projectTaskId}")
    public CompletableFuture<ResponseEntity<Void>> addTask(@PathVariable UUID teamId, @PathVariable UUID projectTaskId) {
        return teamService.addTaskToTeam(new TeamId(teamId), new ProjectTaskId(projectTaskId))
                .thenApply(TeamTaskId::value)
                .thenApply(id -> uri("/teams/" + teamId + "/tasks/" + id))
                .thenApply(this::path);
    }

    @PatchMapping("/{teamId}/tasks/{taskId}/assignee")
    public CompletableFuture<ResponseEntity<Void>> assignTask(@PathVariable UUID teamId, @PathVariable UUID taskId, @RequestBody AssignTaskInput assignTaskInput) {
        return teamService.assignTask(new TeamId(teamId), new TeamTaskId(taskId), new TeamMemberId(assignTaskInput.assigneeId()))
                .thenApply(this::noContent);
    }

    @PostMapping("/{teamId}/tasks/{taskId}/mark-in-progress")
    public CompletableFuture<ResponseEntity<Void>> markTaskInProgress(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        return teamService.markInProgress(new TeamId(teamId), new TeamTaskId(taskId))
                .thenApply(this::noContent);
    }

    @PostMapping("/{teamId}/tasks/{taskId}/unassign")
    public CompletableFuture<ResponseEntity<Void>> markTaskUnAssigned(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        return teamService.unassignTask(new TeamId(teamId), new TeamTaskId(taskId))
                .thenApply(this::noContent);
    }

    @PostMapping("/{teamId}/tasks/{taskId}/complete")
    public CompletableFuture<ResponseEntity<Void>> markTaskCompleted(@PathVariable UUID teamId, @PathVariable UUID taskId, @RequestBody ActualSpentTime actualSpentTime) {
        return teamService.markTaskCompleted(new TeamId(teamId), new TeamTaskId(taskId), new tn.portfolio.axon.common.domain.ActualSpentTime(actualSpentTime.hours(), actualSpentTime.minutes()))
                .thenApply(this::noContent);
    }

    @DeleteMapping("/{teamId}/tasks/{taskId}")
    public CompletableFuture<ResponseEntity<Void>> removeTask(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        return teamService.removeTask(new TeamId(teamId), new TeamTaskId(taskId))
                .thenApply(this::noContent);
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public CompletableFuture<ResponseEntity<Void>> removeMember(@PathVariable UUID teamId, @PathVariable UUID memberId) {
        return teamService.removeTeamMember(new TeamId(teamId), new TeamMemberId(memberId))
                .thenApply(this::noContent);
    }

    @GetMapping
    public ResponseEntity<List<TeamsView>> findAll() {
        return ResponseEntity.ok(teamViewService.findAll());
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamView> findById(@PathVariable UUID teamId) {
        return teamViewService.findById(teamId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
