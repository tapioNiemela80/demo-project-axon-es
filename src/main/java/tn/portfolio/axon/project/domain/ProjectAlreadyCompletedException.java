package tn.portfolio.axon.project.domain;

import tn.portfolio.axon.common.domain.ProjectId;

public class ProjectAlreadyCompletedException extends RuntimeException {

    private final ProjectId projectId;
    public ProjectAlreadyCompletedException(ProjectId projectId) {
        super("Project %s already completed".formatted(projectId));
        this.projectId = projectId;
    }
}