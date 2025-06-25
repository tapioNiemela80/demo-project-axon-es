package tn.portfolio.axon.project.event;

import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ProjectTaskId;

public record ProjectTaskCompletedEvent(ProjectId projectId,
                                        ProjectTaskId taskId,
                                        ActualSpentTime actualSpentTime) {
}
