package tn.portfolio.axon.team.event;

import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;

public record TeamMemberAddedEvent(TeamId teamId, TeamMemberId memberId, String name, String profession) {
}
