package tn.portfolio.axon.team.domain;

public class TaskCannotBeDeletedException extends RuntimeException {
    private final TeamTaskId taskId;
    public TaskCannotBeDeletedException(TeamTaskId taskId) {
        super("Task cannot be deleted %s".formatted(taskId));
        this.taskId = taskId;
    }
}
