package tn.portfolio.axon.project.event;

import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.project.domain.TimeEstimation;

import java.util.UUID;

public record TaskAddedToProjectEvent(ProjectId projectId,
                                      ProjectTaskId taskId,
                                      String name, String description,
                                      TimeEstimation estimation) {
}
