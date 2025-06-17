package tn.portfolio.axon.team.event;

import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record TaskAddedToTeamEvent(TeamId teamId, TeamTaskId teamTaskId, ProjectTaskId projectTaskId, String title, String description) {
}
