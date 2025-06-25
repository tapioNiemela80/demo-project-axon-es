package tn.portfolio.axon.project.event;

import tn.portfolio.axon.project.domain.ProjectId;

public record ProjectCompletedEvent(ProjectId projectId, String name) {
}
