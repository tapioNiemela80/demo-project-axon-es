package tn.portfolio.axon.project.projection;

import jakarta.persistence.*;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "project_tasks", schema = "project_demo_cqrs")
class ProjectTask {

    @Id
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(name = "estimated_time_hours", nullable = false)
    private int estimatedTimeHours;
    @Column(name = "estimated_time_minutes", nullable = false)
    private int estimatedTimeMinutes;
    @Column(name = "task_status", nullable = false)
    private String taskStatus;
    @Column(name = "actual_time_spent_hours")
    private Integer actualTimeSpentHours;
    @Column(name = "actual_time_spent_minutes")
    private Integer actualTimeSpentMinutes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    protected ProjectTask(){
        //for jpa
    }
    private ProjectTask(UUID id, String title, String description, int estimatedTimeHours, int estimatedTimeMinutes, String taskStatus, Project project) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.estimatedTimeHours = estimatedTimeHours;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.taskStatus = taskStatus;
        this.project = project;
    }

    static ProjectTask newInstance(Project project, ProjectTaskId id, String title, String description, TimeEstimation estimation){
        return new ProjectTask(id.value(), title, description, estimation.getHours(), estimation.getMinutes(), "INCOMPLETE", project);
    }

    boolean hasId(ProjectTaskId taskId) {
        return id.equals(taskId.value());
    }

    void markCompleted(ActualSpentTime actualSpentTime) {
        this.taskStatus = "COMPLETE";
        this.actualTimeSpentHours = actualSpentTime.getHours();
        this.actualTimeSpentMinutes = actualSpentTime.getMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectTask other = (ProjectTask) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    ProjectTaskSnapshot toProjectTaskSnapshot(UUID projectId) {
        return new ProjectTaskSnapshot(new ProjectTaskId(this.id), new ProjectId(projectId), title, description, new TimeEstimation(estimatedTimeHours, estimatedTimeMinutes));
    }
}