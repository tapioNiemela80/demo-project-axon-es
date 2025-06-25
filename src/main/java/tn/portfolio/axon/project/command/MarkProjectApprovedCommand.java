package tn.portfolio.axon.project.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.project.domain.ProjectId;

public record MarkProjectApprovedCommand(@TargetAggregateIdentifier ProjectId projectId) {
}
