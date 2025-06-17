package tn.portfolio.axon.approval.domain;

import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.project.event.ProjectInitializedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Saga
public class ProjectApprovalSaga {

    private UUID projectId;
    private List<UUID> approvers;

    static int howMany = 0;

    public ProjectApprovalSaga() {
        System.out.println("ProjectApprovalSaga "+howMany);
        howMany++;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectInitializedEvent event) {
        System.out.println(" START SAGA ! ProjectInitializedEvent2 "+event.projectId());
        this.projectId = event.projectId().value();
        this.approvers = new ArrayList<>();
        System.out.println("this.projectId "+this.projectId);
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

    public List<UUID> getApprovers() {
        return approvers;
    }

    public void setApprovers(List<UUID> approvers) {
        this.approvers = approvers;
    }

    @SagaEventHandler(associationProperty = "projectId", associationResolver = ProjectIdAssociationResolver.class)
    public void on(ProjectApprovementInitializedEvent event){
        //System.out.println("\n"+this+"\n\n>>> ASSOCIATIONS: ");
        //SagaLifecycle.associationValues().stream()
        //                .forEach(ass -> System.out.println("   "+ass.getKey()+" = "+ass.getValue()));
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+event.projectId());

        this.approvers.add(event.approverId().value());

        System.out.println(" ProjectApprovementInitializedEvent LOPPU "+this);
    }

    @Override
    public String toString() {
        return "ProjectApprovalSaga{" +
                "projectId='" + projectId + '\'' +
                ", approvers=" + approvers +
                '}';
    }
}