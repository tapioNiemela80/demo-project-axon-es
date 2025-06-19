package tn.portfolio.axon.approval.domain;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tn.portfolio.axon.approval.event.ProjectApprovedByApproverEvent;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.approval.event.ProjectRejectedByApproverEvent;
import tn.portfolio.axon.project.command.MarkProjectApprovedCommand;
import tn.portfolio.axon.project.command.MarkProjectRejectedCommand;
import tn.portfolio.axon.project.event.ProjectInitializedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Saga
public class ProjectApprovalSaga {

    private static final Logger log = LoggerFactory.getLogger(ProjectApprovalSaga.class);

    private UUID projectId;
    private List<ProjectApprovalsData> projectApprovalsData;

    @Autowired
    private CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectInitializedEvent event) {
        log.info("Saga started on %s".formatted(event.projectId()));
        this.projectId = event.projectId().value();
        this.projectApprovalsData = new ArrayList<>();
        String id = event.projectId().value().toString();
        SagaLifecycle.associateWith("projectId", id);
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public List<ProjectApprovalsData> getProjectApprovalsData() {
        return projectApprovalsData;
    }

    public void setProjectApprovalsData(List<ProjectApprovalsData> projectApprovalsData) {
        this.projectApprovalsData = projectApprovalsData;
    }

    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectApprovementInitializedEvent event) {
        log.info("initialized approver %s on project %s".formatted(event.approverId(), event.projectId()));
        this.projectApprovalsData.add(ProjectApprovalsData.newInstance(event.approverId()));
    }

    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectApprovedByApproverEvent approvedEvent) {
        log.info("project %s approved by %s".formatted(approvedEvent.projectId(), approvedEvent.approverId()));
        this.projectApprovalsData.stream()
                .filter(data -> data.hasApproverId(approvedEvent.approverId()))
                .forEach(ProjectApprovalsData::approve);
        if (this.projectApprovalsData.stream().allMatch(ProjectApprovalsData::isApproved)) {
            log.info("all required approvals gathered, marking project %s approved".formatted(approvedEvent.projectId()));
            commandGateway.send(new MarkProjectApprovedCommand(approvedEvent.projectId()));
            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectRejectedByApproverEvent rejectedEvent) {
        log.info("project %s rejected by %s".formatted(rejectedEvent.projectId(), rejectedEvent.approverId()));
        this.projectApprovalsData.stream()
                .filter(data -> data.hasApproverId(rejectedEvent.approverId()))
                .forEach(ProjectApprovalsData::reject);
        commandGateway.send(new MarkProjectRejectedCommand(rejectedEvent.projectId()));
        SagaLifecycle.end();
    }

    @Override
    public String toString() {
        return "ProjectApprovalSaga{" +
                "projectId=" + projectId +
                ", projectApprovalsData=" + projectApprovalsData +
                '}';
    }
}