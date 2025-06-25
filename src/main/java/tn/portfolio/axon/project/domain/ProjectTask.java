package tn.portfolio.axon.project.domain;

import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.event.TaskAddedToProjectEvent;

record ProjectTask(ProjectTaskId id, String name, String description, TimeEstimation timeEstimation, TaskStatus status,
                   ActualSpentTime actualSpentTime) {

    static ProjectTask from(TaskAddedToProjectEvent event) {
        return new ProjectTask(event.taskId(), event.name(), event.description(), event.estimation(), TaskStatus.INCOMPLETE, null);
    }

    ProjectTask complete(ActualSpentTime actualSpentTime) {
        return new ProjectTask(this.id, this.name, this.description, this.timeEstimation, TaskStatus.COMPLETE, actualSpentTime);
    }

    boolean hasId(ProjectTaskId taskId) {
        return id.equals(taskId);
    }

    boolean isComplete() {
        return status == TaskStatus.COMPLETE;
    }
}
