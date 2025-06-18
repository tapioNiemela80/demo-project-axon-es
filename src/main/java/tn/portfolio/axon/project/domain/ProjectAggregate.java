package tn.portfolio.axon.project.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import tn.portfolio.axon.project.command.*;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.common.domain.ProjectId;
import tn.portfolio.axon.project.event.*;
import tn.portfolio.axon.project.event.ProjectWasApprovedEvent;
import tn.portfolio.axon.project.event.ProjectWasRejectedEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static tn.portfolio.axon.project.domain.ProjectStatus.COMPLETED;
import static tn.portfolio.axon.project.domain.ProjectStatus.PLANNED;

@Aggregate
public class ProjectAggregate {

    @AggregateIdentifier
    private ProjectId projectId;
    private String name;
    private String description;
    private LocalDate estimatedEndDate;
    private TimeEstimation originalEstimation;
    private TimeEstimation currentTotalEstimation;
    private ProjectStatus status;
    private List<ProjectTask> tasks;
    private List<ApproverId> approvers;
    public ProjectAggregate() {
    }

    @CommandHandler
    public ProjectAggregate(InitializeProjectCommand cmd) {
        apply(new ProjectInitializedEvent(
                cmd.projectId(),
                cmd.name(),
                cmd.description(),
                cmd.estimatedEndDate(),
                cmd.estimation()
        ));

        for (ApproverCommandDto approver : cmd.approvers()) {
            apply(new ProjectApproverPlacedEvent(
                    cmd.projectId(),
                    approver.approverId(),
                    approver.name(),
                    approver.role(),
                    approver.email()
            ));
        }
    }

    @CommandHandler
    public void on(AddTaskCommand addTask) {
        if (status != PLANNED) {
            throw new ProjectAlreadyCompletedException(projectId);
        }
        var newEstimation = currentTotalEstimation.add(addTask.estimation());
        if (newEstimation.exceedsOther(originalEstimation)) {
            throw new ProjectTimeEstimationWouldBeExceededException("Cannot add any more tasks, project estimation would be exceeded");
        }
        apply(new TaskAddedToProjectEvent(projectId, addTask.taskId(), addTask.name(), addTask.description(), addTask.estimation()));
    }

    @CommandHandler
    public void on(CompleteTaskCommand completeTask) {
        if (tasks.stream().noneMatch(task -> task.hasId(completeTask.taskId()))) {
            throw new UnknownProjectTaskIdException(completeTask.taskId());
        }
        apply(new ProjectTaskCompletedEvent(projectId, completeTask.taskId(), completeTask.actualSpentTime()));
    }

    @CommandHandler
    public void on(MarkProjectApprovedCommand cmd) {
        if (status != COMPLETED) {
            throw new ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException(projectId);
        }
        apply(new ProjectWasApprovedEvent(projectId));
    }

    @CommandHandler
    public void on(MarkProjectRejectedCommand cmd) {
        if (status != COMPLETED) {
            throw new ProjectNeedsToBeCompletedBeforeApprovalOrRejectionException(projectId);
        }
        apply(new ProjectWasRejectedEvent(projectId));
    }

    @EventSourcingHandler
    public void on(ProjectInitializedEvent event) {
        this.projectId = event.projectId();
        this.name = event.name();
        this.description = event.description();
        this.estimatedEndDate = event.estimatedEndDate();
        this.originalEstimation = event.estimation();
        this.status = PLANNED;
        this.currentTotalEstimation = TimeEstimation.zeroEstimation();
        this.tasks = List.of();
        this.approvers = List.of();
    }

    @EventSourcingHandler
    public void on(ProjectApproverPlacedEvent event) {
        this.approvers = concat(approvers, event.approverId());
    }

    @EventSourcingHandler
    public void on(ProjectWasApprovedEvent event) {
        status = ProjectStatus.APPROVED;
    }

    @EventSourcingHandler
    public void on(ProjectWasRejectedEvent event) {
        status = ProjectStatus.REJECTED;
    }

    @EventSourcingHandler
    public void on(TaskAddedToProjectEvent event) {
        this.tasks = concat(tasks, ProjectTask.from(event));
        this.currentTotalEstimation = this.currentTotalEstimation.add(event.estimation());
    }

    @EventSourcingHandler
    public void on(ProjectTaskCompletedEvent event) {
        this.tasks = tasks.stream()
                .map(processTask(event.taskId(), event.actualSpentTime()))
                .toList();
        if (tasks.stream().allMatch(ProjectTask::isComplete)) {
            apply(new ProjectCompletedEvent(projectId, name));
        }
    }

    @EventSourcingHandler
    public void on(ProjectCompletedEvent event) {
        status = COMPLETED;
    }

    private Function<ProjectTask, ProjectTask> processTask(ProjectTaskId projectTaskId, ActualSpentTime actualSpentTime) {
        return task -> {
            if (task.hasId(projectTaskId)) {
                return task.complete(actualSpentTime);
            }
            return task;
        };
    }

    private <T> List<T> concat(List<T> things, T newThing) {
        return Stream.concat(things.stream(), Stream.of(newThing)).toList();
    }
}