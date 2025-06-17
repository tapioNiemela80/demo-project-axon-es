package tn.portfolio.axon.project.domain;

import tn.portfolio.axon.common.domain.ProjectId;

import java.util.UUID;

public class UnknownProjectIdException extends RuntimeException{
    private final ProjectId givenId;

    public UnknownProjectIdException(ProjectId givenId) {
        super("Unknown project teamId %s".formatted(givenId));
        this.givenId = givenId;
    }
}
