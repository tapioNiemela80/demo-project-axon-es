package tn.portfolio.axon.project.command;

import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.domain.ProjectRole;

public record ApproverCommandDto(
        ApproverId approverId,
        String name,
        ProjectRole role,
        String email
) {}