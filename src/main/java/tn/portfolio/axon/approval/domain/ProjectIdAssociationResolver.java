package tn.portfolio.axon.approval.domain;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.annotation.MessageHandlingMember;
import org.axonframework.modelling.saga.AssociationResolver;
import tn.portfolio.axon.approval.event.ProjectApprovementInitializedEvent;
import tn.portfolio.axon.project.event.ProjectInitializedEvent;

import javax.annotation.Nonnull;

public class ProjectIdAssociationResolver  implements AssociationResolver {

    @Override
    public <T> void validate(@Nonnull String associationPropertyName, @Nonnull MessageHandlingMember<T> handler) {
        //System.out.println("Validating association property: " + associationPropertyName);
    }

    @Override
    public <T> Object resolve(@Nonnull String associationPropertyName, @Nonnull EventMessage<?> message, @Nonnull MessageHandlingMember<T> handler) {
        //System.out.println("resolve association property: " + associationPropertyName);
        if (message.getPayload() instanceof ProjectApprovementInitializedEvent approvementEvent) {
            System.out.println("approvementEvent **** "+approvementEvent.projectId());
            String id =  approvementEvent.projectId().value().toString();
            return id;
        }
        else if (message.getPayload() instanceof ProjectInitializedEvent initializedEvent) {
            System.out.println("initializedEvent **** "+initializedEvent.projectId());
            String id = initializedEvent.projectId().value().toString();
            return id;
        }
        System.out.println("___________________________________ "+message.getPayload().getClass());
        return "FOO";
    }

}
