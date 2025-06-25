package tn.portfolio.axon.approval.service;

import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;

public class ApprovalNotFoundException extends RuntimeException {
    private final ProjectId projectId;
    private final ApproverId approverId;

    public ApprovalNotFoundException(ProjectId projectId, ApproverId approverId) {
        super("Approval not found by %s %s".formatted(projectId, approverId));
        this.projectId = projectId;
        this.approverId = approverId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public ApproverId getApproverId() {
        return approverId;
    }
}
