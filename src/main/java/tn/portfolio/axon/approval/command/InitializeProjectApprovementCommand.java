package tn.portfolio.axon.approval.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.approval.domain.ApprovalId;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.domain.ProjectRole;

public record InitializeProjectApprovementCommand(
        @TargetAggregateIdentifier ApprovalId approvalId,
        ApproverId approverId,
        ProjectId projectId,
        String approverName,
        ProjectRole role,
        String approverEmail
) {
}
