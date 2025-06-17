package tn.portfolio.axon.project.controller;

import tn.portfolio.axon.project.domain.ProjectRole;

public record ProjectApprover(String name, ProjectRole role, String email) {}