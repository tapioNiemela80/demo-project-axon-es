package tn.portfolio.axon.team.domain;

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
}
