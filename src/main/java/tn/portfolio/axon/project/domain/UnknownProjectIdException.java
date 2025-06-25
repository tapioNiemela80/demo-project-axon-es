package tn.portfolio.axon.project.domain;

public class UnknownProjectIdException extends RuntimeException {
    private final ProjectId projectId;

    public UnknownProjectIdException(ProjectId projectId) {
        super("Unknown project teamId %s".formatted(projectId));
        this.projectId = projectId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }
}
