package tn.portfolio.axon.project.event;

import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;

public record ProjectApprovedEvent(ProjectId projectId,
                                   ApproverId approverId) {
}
