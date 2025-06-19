package tn.portfolio.axon.team.domain;

import java.util.Objects;

class TeamMember {
    private final TeamMemberId id;

    private final String name;

    private final String profession;

    TeamMember(TeamMemberId id, String name, String profession) {
        this.id = id;
        this.name = name;
        this.profession = profession;
    }

    static TeamMember createNew(TeamMemberId memberId, String name, String profession) {
        return new TeamMember(memberId, name, profession);
    }

    boolean hasId(TeamMemberId expected) {
        return id.equals(expected);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember that = (TeamMember) o;
        return id.equals(that.id) && name.equals(that.name) && profession.equals(that.profession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, profession);
    }
}
