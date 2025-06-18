package tn.portfolio.axon.team.projection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tn.portfolio.axon.project.domain.ProjectTaskId;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "team_view_project_tasks", schema = "project_demo_cqrs")
public class TeamViewToProjectTask {

    @Id
    private UUID taskId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "estimation_hours", nullable = false)
    private int estimationHours;

    @Column(name = "estimation_minutes", nullable = false)
    private int estimationMinutes;

    protected TeamViewToProjectTask(){
        //for jpa
    }

    public TeamViewToProjectTask(UUID taskId, UUID projectId, String name, String description, int estimationHours, int estimationMinutes) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.estimationHours = estimationHours;
        this.estimationMinutes = estimationMinutes;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getEstimationHours() {
        return estimationHours;
    }

    public int getEstimationMinutes() {
        return estimationMinutes;
    }

    public ProjectTaskId getProjectTaskId() {
        return new ProjectTaskId(taskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamViewToProjectTask other = (TeamViewToProjectTask) o;
        return Objects.equals(taskId, other.taskId);
    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }
}