package tn.portfolio.axon.team.domain;

import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.project.domain.ProjectTaskId;

import java.util.List;
import java.util.Objects;

class TeamTask {
    private final TeamTaskId id;
    private final ProjectTaskId projectTaskId;
    private final String name;
    private final String description;
    private final TeamTaskStatus status;
    private final TeamMemberId assigneeId;
    private final ActualSpentTime actualSpentTime;

    private TeamTask(TeamTaskId id, ProjectTaskId projectTaskId, String name, String description, TeamTaskStatus status, TeamMemberId assigneeId, ActualSpentTime actualSpentTime) {
        this.id = id;
        this.projectTaskId = projectTaskId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.actualSpentTime = actualSpentTime;
    }

    boolean canBeDeleted(){
        return status == TeamTaskStatus.NOT_ASSIGNED;
    }

    void verifyCanBeAssignedTo(TeamMemberId memberId, List<TeamMember> currentMembers){
        if (this.status != TeamTaskStatus.NOT_ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task already assigned or in progress.");
        }
        if(currentMembers.stream().noneMatch(member -> member.hasId(memberId))){
            throw new UnknownTeamMemberIdException(memberId);
        }
    }

    TeamTask assignTo(TeamMemberId assigneeId){
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.ASSIGNED, assigneeId, actualSpentTime);
    }

    void verifyCanBeMarkedInProgress(){
        if (this.status != TeamTaskStatus.ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task needs to be assigned before it can be put to in progress.");
        }
    }

    TeamTask markInProgress(){
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.IN_PROGRESS, assigneeId, actualSpentTime);
    }

    void verifyCanBeCompleted(){
        if (this.status != TeamTaskStatus.IN_PROGRESS) {
            throw new TaskTransitionNotAllowedException("task not in progress");
        }
    }

    TeamTask complete(ActualSpentTime actualTimeSpent) {
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.COMPLETED, null, actualTimeSpent);
    }

    void verifyCanBeUnassigned(){
        if(this.status != TeamTaskStatus.ASSIGNED){
            throw new TaskTransitionNotAllowedException("Task is not assigned");
        }
    }

    TeamTask unassign() {
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.NOT_ASSIGNED, null, actualSpentTime);
    }

    boolean hasId(TeamTaskId expected) {
        return id.equals(expected);
    }

    boolean isAssignedTo(TeamMemberId memberId) {
        Objects.requireNonNull(memberId);
        if(assigneeId == null){
            return false;
        }
        return assigneeId.equals(memberId);
    }

    ProjectTaskId getOriginalTaskId() {
        return projectTaskId;
    }

    public static TeamTask newInstance(TeamTaskId id, ProjectTaskId projectTaskId, String name, String description){
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.NOT_ASSIGNED, null, null);
    }

}