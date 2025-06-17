package tn.portfolio.axon.team.event;

import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record TaskRemovedFromTeamEvent(TeamId teamId, TeamTaskId taskId) {
}
