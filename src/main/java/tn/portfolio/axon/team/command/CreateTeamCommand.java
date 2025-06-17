package tn.portfolio.axon.team.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.team.domain.TeamId;

public record CreateTeamCommand(@TargetAggregateIdentifier TeamId id, String name) {
}
