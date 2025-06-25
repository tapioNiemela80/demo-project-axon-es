package tn.portfolio.axon.approval.event;

import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;

public record ProjectApprovedByApproverEvent(ProjectId projectId, ApproverId approverId) {
}
