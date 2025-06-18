package tn.portfolio.axon.approval.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.portfolio.axon.approval.domain.ApprovalId;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;

public record ApproveProjectByApproverCommand(@TargetAggregateIdentifier ApprovalId approvalId, ProjectId projectId, ApproverId approverId) {
}
