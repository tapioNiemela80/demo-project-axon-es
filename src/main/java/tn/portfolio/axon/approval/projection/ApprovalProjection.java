package tn.portfolio.axon.approval.projection;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.portfolio.axon.approval.event.ProjectApprovedByApproverEvent;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.approval.event.ProjectRejectedByApproverEvent;
import tn.portfolio.axon.common.service.DateService;

import java.time.Instant;
import java.util.UUID;

@Component
@ProcessingGroup("approval-projection")
public class ApprovalProjection {
    private final ApprovalRepository approvals;
    private final DateService dateService;

    public ApprovalProjection(ApprovalRepository approvals, DateService dateService) {
        this.approvals = approvals;
        this.dateService = dateService;
    }

    @ResetHandler
    public void reset() {
        approvals.deleteAllInBatch();
    }

    @EventHandler
    public void on(ProjectApprovementInitializedEvent event){
        approvals.save(Approval.newInstance(event.approvalId().value(), event.approverId().value(),
                event.projectId().value(),
                event.approverName(), event.role().name(), event.approverEmail()));
    }

    @EventHandler
    @Transactional
    public void on(ProjectApprovedByApproverEvent event, @Timestamp Instant when){
        approvals.findByProjectIdAndApproverId(event.projectId().value(), event.approverId().value())
                .ifPresent(approval -> approval.markApproved(dateService.toLocalDateTime(when)));
    }

    @EventHandler
    @Transactional
    public void on(ProjectRejectedByApproverEvent event, @Timestamp Instant when){
        approvals.findByProjectIdAndApproverId(event.projectId().value(), event.approverId().value())
                .ifPresent(approval -> approval.markRejected(dateService.toLocalDateTime(when), event.reason()));
    }

}
