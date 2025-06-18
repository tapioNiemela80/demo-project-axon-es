package tn.portfolio.axon.project.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.common.domain.ProjectId;
public record MarkProjectApprovedCommand(@TargetAggregateIdentifier ProjectId projectId) {
}
