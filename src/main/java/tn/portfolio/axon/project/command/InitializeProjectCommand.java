package tn.portfolio.axon.project.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.TimeEstimation;

import java.time.LocalDate;
import java.util.Set;

public record InitializeProjectCommand(
        @TargetAggregateIdentifier ProjectId projectId,
        String name,
        String description,
        LocalDate estimatedEndDate,
        TimeEstimation estimation,
        Set<ApproverCommandDto> approvers
) {}