package tn.portfolio.axon.approval.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import tn.portfolio.axon.approval.command.ApproveProjectByApproverCommand;
import tn.portfolio.axon.approval.command.InitializeProjectApprovementCommand;
import tn.portfolio.axon.approval.command.RejectProjectByApproverCommand;
import tn.portfolio.axon.approval.event.ProjectApprovedByApproverEvent;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.approval.event.ProjectRejectedByApproverEvent;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.ApproverId;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ApprovalAggregate {
    @AggregateIdentifier
    private ApprovalId approvalId;
    private ApproverId approverId;
    private ProjectId projectId;
    private ApprovalStatus status;

    public ApprovalAggregate() {
    }

    @CommandHandler
    public ApprovalAggregate(InitializeProjectApprovementCommand cmd) {
        apply(new ProjectApprovementInitializedEvent(cmd.approvalId(), cmd.approverId(), cmd.projectId(), cmd.approverName(), cmd.role(), cmd.approverEmail()));
    }

    @CommandHandler
    public void on(ApproveProjectByApproverCommand cmd) {
        if (status != ApprovalStatus.PENDING) {
            throw new ApprovalStateChangeNotAllowedException("Cannot change approval status of %s on project %s".formatted(approverId, projectId));
        }
        apply(new ProjectApprovedByApproverEvent(cmd.projectId(), cmd.approverId()));
    }

    @CommandHandler
    public void on(RejectProjectByApproverCommand cmd) {
        if (status != ApprovalStatus.PENDING) {
            throw new ApprovalStateChangeNotAllowedException("Cannot change approval status of %s on project %s".formatted(approverId, projectId));
        }
        apply(new ProjectRejectedByApproverEvent(cmd.projectId(), cmd.approverId(), cmd.reason()));
    }

    @EventSourcingHandler
    public void on(ProjectApprovementInitializedEvent event) {
        this.approvalId = event.approvalId();
        this.status = ApprovalStatus.PENDING;
        this.projectId = event.projectId();
        this.approverId = event.approverId();
    }

    @EventSourcingHandler
    public void on(ProjectApprovedByApproverEvent event) {
        this.status = ApprovalStatus.APPROVED;
    }

    @EventSourcingHandler
    public void on(ProjectRejectedByApproverEvent event) {
        this.status = ApprovalStatus.REJECTED;
    }
}
