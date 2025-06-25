package tn.portfolio.axon.project.projection;

import jakarta.persistence.*;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "projects", schema = "project_demo_cqrs")
public class Project {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    private String description;
    private String status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;
    @Column(name = "initial_estimated_time_hours", nullable = false)
    private int initialEstimatedTimeHours;
    @Column(name = "initial_estimated_time_minutes", nullable = false)
    private int initialEstimatedTimeMinutes;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> tasks = new ArrayList<>();

    protected Project(){
        //for jpa
    }

    public Project(ProjectId id, String name, String description, LocalDateTime createdAt, LocalDate plannedEndDate, TimeEstimation estimation) {
        this.id = id.value();
        this.name = name;
        this.description = description;
        this.status = "PLANNED";
        this.createdAt = createdAt;
        this.plannedEndDate = plannedEndDate;
        this.initialEstimatedTimeHours = estimation.getHours();
        this.initialEstimatedTimeMinutes = estimation.getMinutes();
    }
    public void addTask(ProjectTaskId id, String title, String description, TimeEstimation estimation) {
        tasks.add(ProjectTask.newInstance(this, id, title, description, estimation));
    }

    public void markTaskCompleted(ProjectTaskId taskId, ActualSpentTime actualSpentTime){
        tasks.stream()
                .filter(task -> task.hasId(taskId))
                .forEach(task -> task.markCompleted(actualSpentTime));
    }

    public void markCompleted() {
        this.status ="COMPLETED";
    }

    public ProjectId getId() {
        return new ProjectId(id);
    }

    public void markApproved() {
        this.status = "APPROVED";
    }

    public void markRejected() {
        this.status = "REJECTED";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project other = (Project) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}