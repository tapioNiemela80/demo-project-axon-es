package tn.portfolio.axon.team.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.team.command.*;
import tn.portfolio.axon.team.event.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class TeamAggregate {
    @AggregateIdentifier
    private TeamId id;
    private String name;
    private List<TeamMember> members;
    private List<TeamTask> tasks;

    public TeamAggregate() {
    }

    @CommandHandler
    public TeamAggregate(CreateTeamCommand cmd){
        apply(new TeamCreatedEvent(cmd.id(), cmd.name()));
    }

    @CommandHandler
    public void on(AddTeamMemberCommand cmd){
        apply(new TeamMemberAddedEvent(cmd.id(), cmd.teamMemberId(), cmd.name(), cmd.profession()));
    }

    @CommandHandler
    public void on(AddTeamTaskCommand cmd){
        apply(new TaskAddedToTeamEvent(cmd.id(), cmd.teamTaskId(), cmd.projectTaskId(), cmd.name(), cmd.description()));
    }

    @CommandHandler
    public void on(MarkTaskInProgressCommand cmd){
        TeamTask task = getTask(cmd.taskId());
        task.verifyCanBeMarkedInProgress();
        apply(new TaskMarkedInProgressEvent(id, cmd.taskId()));
    }

    @CommandHandler
    public void on(AssignTaskCommand cmd){
        TeamTask task = getTask(cmd.taskId());
        task.verifyCanBeAssignedTo(cmd.memberId(), this.members);
        apply(new TeamTaskAssignedEvent(cmd.id(), cmd.taskId(), cmd.memberId()));
    }

    @CommandHandler
    public void on(UnassignTaskCommand cmd){
        TeamTask task = getTask(cmd.taskId());
        task.verifyCanBeUnassigned();
        apply(new TaskUnassignedEvent(cmd.id(), cmd.taskId()));
    }

    @CommandHandler
    public void on(MarkTaskCompletedCommand cmd){
        TeamTask task = getTask(cmd.taskId());
        task.verifyCanBeCompleted();
        apply(new TaskCompletedEvent(cmd.id(), cmd.taskId(), task.getOriginalTaskId(), cmd.actualSpentTime()));
    }

    @CommandHandler
    public void on(RemoveTaskCommand cmd){
        TeamTask task = getTask(cmd.taskId());
        if(!task.canBeDeleted()){
            throw new TaskCannotBeDeletedException(cmd.taskId());
        }
        apply(new TaskRemovedFromTeamEvent(id, cmd.taskId()));
    }

    private TeamTask getTask(TeamTaskId id){
     return tasks.stream()
             .filter(aTask -> aTask.hasId(id))
             .findFirst()
             .orElseThrow(() -> new UnknownTeamTaskIdException(id));
    }

    @CommandHandler
    public void on(RemoveTeamMemberCommand cmd){
        Objects.requireNonNull(cmd.memberId());
        if(members.stream().noneMatch(aMember -> aMember.hasId(cmd.memberId()))) {
            throw new UnknownTeamMemberIdException(cmd.memberId());
        }
        verifyMemberCanBeRemoved(cmd.memberId());
        apply(new TeamMemberRemovedEvent(id, cmd.memberId()));
    }

    private void verifyMemberCanBeRemoved(TeamMemberId memberId) {
        if(tasks.stream().anyMatch(task -> task.isAssignedTo(memberId))){
            throw new TeamMemberHasAssignedTasksException(memberId);
        }
    }

    @EventSourcingHandler
    public void on(TeamCreatedEvent event){
        this.id = event.teamId();
        this.name = event.name();
        this.members = List.of();
        this.tasks = List.of();
    }

    @EventSourcingHandler
    public void on(TeamMemberAddedEvent event){
        this.members = concat(members, TeamMember.createNew(event.memberId(), event.name(), event.profession()));
    }

    @EventSourcingHandler
    public void on(TeamMemberRemovedEvent event){
        this.members = members.stream().filter(aMember -> !aMember.hasId(event.memberId())).toList();
    }

    @EventSourcingHandler
    public void on(TaskAddedToTeamEvent event){
        this.tasks = concat(tasks, TeamTask.newInstance(event.teamTaskId(), event.projectTaskId(), event.title(), event.description()));
    }

    @EventSourcingHandler
    public void on(TaskRemovedFromTeamEvent event){
        this.tasks = tasks.stream().filter(task -> !task.hasId(event.taskId())).toList();
    }

    @EventSourcingHandler
    public void on(TeamTaskAssignedEvent event){
        this.tasks = tasks.stream().map((assign(event.taskId(), event.memberId()))).toList();
    }

    @EventSourcingHandler
    public void on(TaskUnassignedEvent event){
        this.tasks = tasks.stream().map((markUnassigned(event.taskId()))).toList();
    }

    @EventSourcingHandler
    public void on(TaskMarkedInProgressEvent event){
        this.tasks = tasks.stream().map((markInProgress(event.taskId()))).toList();
    }

    @EventSourcingHandler
    public void on(TaskCompletedEvent event){
        this.tasks = tasks.stream().map((markCompleted(event.teamTaskId(), event.actualTimeSpent()))).toList();
    }

    private Function<TeamTask, TeamTask> markUnassigned(TeamTaskId taskId) {
        return teamTask -> {
            if(teamTask.hasId(taskId)){
                return teamTask.unassign();
            }
            return teamTask;
        };
    }

    private Function<TeamTask, TeamTask> markInProgress(TeamTaskId taskId) {
        return teamTask -> {
            if(teamTask.hasId(taskId)){
                return teamTask.markInProgress();
            }
            return teamTask;
        };
    }

    private Function<TeamTask, TeamTask> markCompleted(TeamTaskId taskId, ActualSpentTime actualSpentTime) {
        return teamTask -> {
            if(teamTask.hasId(taskId)){
                return teamTask.complete(actualSpentTime);
            }
            return teamTask;
        };
    }

    private Function<TeamTask, TeamTask> assign(TeamTaskId taskId, TeamMemberId memberId) {
        return teamTask -> {
            if(teamTask.hasId(taskId)){
                return teamTask.assignTo(memberId);
            }
            return teamTask;
        };
    }


    private <T> List<T> concat(List<T> things, T newThing) {
        return Stream.concat(things.stream(), Stream.of(newThing)).toList();
    }

}
