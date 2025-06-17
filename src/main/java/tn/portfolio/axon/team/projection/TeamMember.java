package tn.portfolio.axon.team.projection;

import jakarta.persistence.*;
import tn.portfolio.axon.team.domain.TeamMemberId;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "team_members", schema = "project_demo_cqrs")
class TeamMember {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String profession;

    protected TeamMember(){
        //for jpa
    }

    private TeamMember(UUID id, Team team, String name, String profession) {
        this.id = id;
        this.team = team;
        this.name = name;
        this.profession = profession;
    }

    static TeamMember newInstance(TeamMemberId id, Team team, String name, String profession){
        return new TeamMember(id.value(), team, name, profession);
    }

    void removeTeam() {
        this.team = null;
    }

    boolean hasId(TeamMemberId memberId) {
        return memberId.value().equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember other = (TeamMember) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}