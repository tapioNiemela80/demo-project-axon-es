package tn.portfolio.axon.team.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record RemoveTaskCommand(@TargetAggregateIdentifier TeamId id, TeamTaskId taskId) {
}
