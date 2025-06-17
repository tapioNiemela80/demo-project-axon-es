package tn.portfolio.axon.project.event;

import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.domain.TimeEstimation;

import java.time.LocalDate;

public record ProjectInitializedEvent(
        ProjectId projectId,
        String name,
        String description,
        LocalDate estimatedEndDate,
        TimeEstimation estimation
) {


}