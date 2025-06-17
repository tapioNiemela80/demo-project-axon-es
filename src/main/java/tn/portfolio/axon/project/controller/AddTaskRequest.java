package tn.portfolio.axon.project.controller;

public record AddTaskRequest(
        String name,
        String description,
        TimeEstimation estimation
) {}