package tn.portfolio.axon.approval.domain;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import tn.portfolio.axon.approval.event.ProjectApprovedByApproverEvent;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.approval.event.ProjectRejectedByApproverEvent;
import tn.portfolio.axon.project.command.MarkProjectApprovedCommand;
import tn.portfolio.axon.project.event.ProjectInitializedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Saga
public class ProjectApprovalSaga {

    private UUID projectId;
    private List<ProjectApprovalsData> projectApprovalsData;

    @Autowired
    private CommandGateway commandGateway;

    static int howMany = 0;

    public ProjectApprovalSaga() {
        System.out.println("ProjectApprovalSaga " + howMany);
        howMany++;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectInitializedEvent event) {
        System.out.println(" START SAGA ! ProjectInitializedEvent2 " + event.projectId());
        this.projectId = event.projectId().value();
        this.projectApprovalsData = new ArrayList<>();
        System.out.println("this.projectId " + this.projectId);
        String id = event.projectId().value().toString();
        //  System.out.println("onProjectInitializedEvent > "+teamId);
        SagaLifecycle.associateWith("projectId", id);

        //SagaLifecycle.associationValues().stream()
        //        .forEach(ass -> System.out.println("  ProjectInitializedEvent>  "+ass.getKey()+" = "+ass.getValue()));

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
        this.projectApprovalsData.add(ProjectApprovalsData.newInstance(event.approverId()));
        System.out.println("------------------------------------------->");
        System.out.println("------------------------------------------->");
        System.out.println("------------------------------------------->");
        System.out.println(" ProjectApprovementInitializedEvent LOPPU " + this);
        System.out.println("<-------------------------------------------");
    }

    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectApprovedByApproverEvent approvedEvent) {
        System.out.println("** "+approvedEvent);
        this.projectApprovalsData.stream()
                .filter(data -> data.hasApproverId(approvedEvent.approverId()))
                .forEach(data -> data.approve());
        if (this.projectApprovalsData.stream().allMatch(data -> data.isApproved())) {
            System.out.println("ALL APPROVERS CLEAR");
            commandGateway.send(new MarkProjectApprovedCommand(approvedEvent.projectId()));
            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectRejectedByApproverEvent rejectedEvent) {
        this.projectApprovalsData.stream()
                .filter(data -> data.hasApproverId(rejectedEvent.approverId()))
                .forEach(data -> data.reject());
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