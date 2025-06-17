package tn.portfolio.axon.approval.projection;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;

import java.util.UUID;

@Component
@ProcessingGroup("approval-projection")
public class ApprovalProjection {
    private final ApprovalRepository approvals;

    public ApprovalProjection(ApprovalRepository approvals) {
        this.approvals = approvals;
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


}
