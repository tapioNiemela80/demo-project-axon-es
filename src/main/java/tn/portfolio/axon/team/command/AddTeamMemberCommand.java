package tn.portfolio.axon.team.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;

public record AddTeamMemberCommand(@TargetAggregateIdentifier TeamId id, TeamMemberId teamMemberId, String name,
                                   String profession) {
}
