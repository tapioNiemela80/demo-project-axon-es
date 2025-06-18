package tn.portfolio.axon.approval.domain;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.annotation.MessageHandlingMember;
import org.axonframework.modelling.saga.AssociationResolver;
import tn.portfolio.axon.approval.event.ProjectApprovedByApproverEvent;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.approval.event.ProjectRejectedByApproverEvent;
import tn.portfolio.axon.project.event.ProjectInitializedEvent;

import javax.annotation.Nonnull;

public class ProjectIdAssociationResolver  implements AssociationResolver {

    @Override
    public <T> void validate(@Nonnull String associationPropertyName, @Nonnull MessageHandlingMember<T> handler) {
    }

    @Override
    public <T> Object resolve(@Nonnull String associationPropertyName, @Nonnull EventMessage<?> message, @Nonnull MessageHandlingMember<T> handler) {
        if (message.getPayload() instanceof ProjectApprovementInitializedEvent approvementEvent) {
            return approvementEvent.projectId().value().toString();
        }
        else if (message.getPayload() instanceof ProjectInitializedEvent initializedEvent) {
            return initializedEvent.projectId().value().toString();
        }
        else if(message.getPayload() instanceof ProjectApprovedByApproverEvent approvedEvent){
            return approvedEvent.projectId().value().toString();
        }

        else if(message.getPayload() instanceof ProjectRejectedByApproverEvent rejectedEvent){
            return rejectedEvent.projectId().value().toString();
        }
        throw new IllegalArgumentException("Unknown message %s".formatted(message.getPayload().getClass()));
    }

}
