package tn.portfolio.axon.project.event;

import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;

public record TaskAddedToProjectEvent(ProjectId projectId,
                                      ProjectTaskId taskId,
                                      String name, String description,
                                      TimeEstimation estimation) {
}
