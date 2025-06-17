package tn.portfolio.axon.team.event;

import tn.portfolio.axon.team.domain.TeamId;

public record TeamCreatedEvent(TeamId teamId, String name) {
}
