package tn.portfolio.axon.team.projection;

import jakarta.persistence.*;
import tn.portfolio.axon.common.domain.ActualSpentTime;
import tn.portfolio.axon.team.domain.TeamId;
import tn.portfolio.axon.team.domain.TeamMemberId;
import tn.portfolio.axon.team.domain.TeamTaskId;

import java.util.*;

@Entity
@Table(name = "teams", schema = "project_demo_cqrs")
public class Team {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> members = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamTask> tasks = new HashSet<>();

    protected Team() {//for jpa
    }

    public Team(TeamId id, String name) {
        this.id = id.value();
        this.name = name;
    }

    public void addMember(TeamMemberId memberId, String name, String profession) {
        members.add(TeamMember.newInstance(memberId, this, name, profession));
    }

    public void removeMember(TeamMemberId memberId) {
        TeamMember member = members.stream().filter(mbmr -> mbmr.hasId(memberId)).findFirst().orElse(null);
        if (member != null) {
            members.remove(member);
            member.removeTeam();
        }
    }

    public void addTask(TeamTaskId taskId, String title, UUID projectTaskId, String description) {
        tasks.add(TeamTask.newInstance(taskId, this, projectTaskId, title, description));
    }

    public void removeTask(TeamTaskId taskId) {
        TeamTask task = tasks.stream().filter(teamTask -> teamTask.hasId(taskId)).findFirst().orElse(null);
        if (task != null) {
            tasks.remove(task);
            task.removeTeam();
        }
    }

    public void assignTask(TeamTaskId taskId, TeamMemberId memberId) {
        findTask(taskId)
                .ifPresent(task -> task.assign(memberId));
    }

    public void markUnassigned(TeamTaskId taskId) {
        findTask(taskId)
                .ifPresent(task -> task.markUnAssigned());
    }

    public void markCompleted(TeamTaskId taskId, ActualSpentTime actualSpentTime) {
        findTask(taskId)
                .ifPresent(task -> task.markCompleted(actualSpentTime));
    }

    public void markTaskInProgress(TeamTaskId taskId) {
        findTask(taskId)
                .ifPresent(task -> task.markInProgress());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team other = (Team) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private Optional<TeamTask> findTask(TeamTaskId taskId) {
        return tasks.stream().filter(task -> task.hasId(taskId)).findFirst();
    }
}