package tn.portfolio.axon.team.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamTaskId;

public record AddTeamTaskCommand(@TargetAggregateIdentifier TeamId id, TeamTaskId teamTaskId, ProjectTaskId projectTaskId, String name, String description) {
}
