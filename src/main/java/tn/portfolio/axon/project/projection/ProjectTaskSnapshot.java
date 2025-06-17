package tn.portfolio.axon.project.projection;

import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;

public record ProjectTaskSnapshot(ProjectTaskId projectTaskId, ProjectId projectId, String title, String description, TimeEstimation timeEstimation) {
}