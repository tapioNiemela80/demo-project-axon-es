package tn.portfolio.axon.team.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record MarkTaskCompletedCommand(@TargetAggregateIdentifier TeamId id, TeamTaskId taskId,
                                       ActualSpentTime actualSpentTime) {
}
