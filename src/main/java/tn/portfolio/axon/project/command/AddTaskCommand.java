package tn.portfolio.axon.project.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;

public record AddTaskCommand(
        @TargetAggregateIdentifier
        ProjectId projectId,
        ProjectTaskId taskId,
        String name,
        String description,
        TimeEstimation estimation) {
}
