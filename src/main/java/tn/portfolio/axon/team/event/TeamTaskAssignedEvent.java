package tn.portfolio.axon.team.event;

import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record TeamTaskAssignedEvent(TeamId teamId, TeamTaskId taskId, TeamMemberId memberId) {
}
