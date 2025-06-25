package tn.portfolio.axon.project.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;

public record CompleteTaskCommand(@TargetAggregateIdentifier
                                  ProjectId projectId,
                                  ProjectTaskId taskId,
                                  ActualSpentTime actualSpentTime) {
}
