package tn.portfolio.axon.project.controller;

import java.time.LocalDate;
import java.util.Set;

public record ProjectInput(
        String name,
        String description,
        LocalDate estimatedEndDate,
        TimeEstimation estimation,
        Set<ProjectApprover> projectApprovers
) {}