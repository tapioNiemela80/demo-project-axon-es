package tn.portfolio.axon.team.domain;

public class TaskAlreadyAssignedException extends RuntimeException {
    public TaskAlreadyAssignedException(String message) {
        super(message);
    }
}
