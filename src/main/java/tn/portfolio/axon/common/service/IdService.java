package tn.portfolio.axon.common.service;

import org.springframework.stereotype.Service;
import tn.portfolio.axon.approval.domain.ApprovalId;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.domain.ProjectTaskId;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;
import tn.portfolio.axon.team.domain.TeamTaskId;

import java.util.UUID;

@Service
public class IdService {
    public ProjectId newProjectId() {
        return new ProjectId(UUID.randomUUID());
    }

    public ApproverId newApproverId() {
        return new ApproverId(UUID.randomUUID());
    }

    public ApprovalId newApprovalId() {
        return new ApprovalId(UUID.randomUUID());
    }

    public ProjectTaskId newProjectTaskId() {
        return new ProjectTaskId(UUID.randomUUID());
    }

    public TeamId newTeamId() {
        return new TeamId(UUID.randomUUID());
    }

    public TeamMemberId newTeamMemberId() {
        return new TeamMemberId(UUID.randomUUID());
    }

    public TeamTaskId newTeamTaskId() {
        return new TeamTaskId(UUID.randomUUID());
    }
}
