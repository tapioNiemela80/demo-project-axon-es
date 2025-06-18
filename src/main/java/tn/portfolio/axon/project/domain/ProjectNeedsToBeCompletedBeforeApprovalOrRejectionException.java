package tn.portfolio.axon.project.domain;

import tn.portfolio.axon.common.domain.ProjectId;

public class ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException extends RuntimeException {
    private final ProjectId projectId;
    public ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException(ProjectId projectId) {
        super("Project %s needs to be completed before approval or rejected".formatted(projectId));
        this.projectId = projectId;
    }
}
