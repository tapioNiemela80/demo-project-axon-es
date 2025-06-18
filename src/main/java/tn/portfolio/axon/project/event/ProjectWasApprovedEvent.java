package tn.portfolio.axon.project.event;

import tn.portfolio.axon.common.domain.ProjectId;
public record ProjectWasApprovedEvent(ProjectId projectId){
}
