package tn.portfolio.axon.approval.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import tn.portfolio.axon.approval.command.InitializeProjectApprovementCommand;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.project.event.ProjectInitializedEvent;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ApprovalAggregate {
    @AggregateIdentifier
    private ApprovalId approvalId;

    public ApprovalAggregate() {
    }

    @CommandHandler
    public ApprovalAggregate(InitializeProjectApprovementCommand cmd){
        apply(new ProjectApprovementInitializedEvent(cmd.approvalId(), cmd.approverId(), cmd.projectId(),cmd.approverName(), cmd.role(), cmd.approverEmail()));
    }

    @EventSourcingHandler
    public void on(ProjectApprovementInitializedEvent event) {
        this.approvalId = event.approvalId();
    }
}
