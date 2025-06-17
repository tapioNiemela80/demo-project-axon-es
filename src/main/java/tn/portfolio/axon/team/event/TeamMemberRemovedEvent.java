package tn.portfolio.axon.team.event;

import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;

public record TeamMemberRemovedEvent(TeamId teamId, TeamMemberId memberId) {
}
