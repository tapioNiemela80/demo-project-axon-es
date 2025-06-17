package tn.portfolio.axon.approval.event;

import tn.portfolio.axon.approval.domain.ApprovalId;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.domain.ProjectRole;

public record ProjectApprovementInitializedEvent(
        ApprovalId approvalId,
        ApproverId approverId,
        ProjectId projectId,
        String approverName,
        ProjectRole role,
        String approverEmail) {


}
