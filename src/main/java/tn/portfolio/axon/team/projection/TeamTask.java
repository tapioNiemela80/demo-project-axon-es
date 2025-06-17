package tn.portfolio.axon.team.projection;

import jakarta.persistence.*;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.team.domain.TeamMemberId;
import tn.portfolio.axon.team.domain.TeamTaskId;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "team_tasks", schema = "project_demo_cqrs")
class TeamTask {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "project_task_id", nullable = false)
    private UUID projectTaskId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(name = "assignee_id")
    private UUID assigneeId;

    @Column(name = "actual_time_spent_hours")
    private Integer actualTimeSpentHours;

    @Column(name = "actual_time_spent_minutes")
    private Integer actualTimeSpentMinutes;

    protected TeamTask() {
        //for jpa
    }

    private TeamTask(UUID id, Team team, UUID projectTaskId, String name, String description, String status, UUID assigneeId, Integer actualTimeSpentHours, Integer actualTimeSpentMinutes) {
        this.id = id;
        this.team = team;
        this.projectTaskId = projectTaskId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.actualTimeSpentHours = actualTimeSpentHours;
        this.actualTimeSpentMinutes = actualTimeSpentMinutes;
    }

    static TeamTask newInstance(TeamTaskId id, Team team, UUID projectTaskId, String name, String description) {
        return new TeamTask(id.value(), team, projectTaskId, name, description, "NOT_ASSIGNED", null, null, null);
    }

    void removeTeam() {
        this.team = null;
    }

    boolean hasId(TeamTaskId taskId) {
        return taskId.value().equals(id);
    }

    void assign(TeamMemberId memberId) {
        this.assigneeId = memberId.value();
        this.status = "ASSIGNED";
    }

    void markUnAssigned() {
        this.status = "NOT_ASSIGNED";
    }

    void markCompleted(ActualSpentTime actualSpentTime) {
        this.assigneeId = null;
        this.status = "COMPLETE";
        this.actualTimeSpentHours = actualSpentTime.getHours();
        this.actualTimeSpentMinutes = actualSpentTime.getMinutes();
    }

    void markInProgress() {
        this.status = "IN_PROGRESS";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamTask other = (TeamTask) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}