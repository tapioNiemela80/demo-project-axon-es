package tn.portfolio.axon.team.event;

import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record TaskMarkedInProgressEvent(TeamId teamId, TeamTaskId taskId) {
}
