package tn.portfolio.axon.project.event;

import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.domain.ProjectRole;

public record ProjectApproverPlacedEvent(
        ProjectId projectId,
        ApproverId approverId,
        String name,
        ProjectRole role,
        String email
) {}