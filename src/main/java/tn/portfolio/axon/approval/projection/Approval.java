package tn.portfolio.axon.approval.projection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "approvals", schema = "project_demo_cqrs")
public class Approval {
    @Id
    private UUID id;
    private UUID projectId;
    private UUID approverId;
    private String name;
    private String email;
    private String approvalStatus;
    private String projectRole;
    private LocalDateTime decisionDate;
    private String decisionReason;

    protected Approval() {
        //for jpa
    }

    private Approval(UUID id, UUID approverId, UUID projectId, String name, String email, String projectRole){
        this.id = id;
        this.approverId = approverId;
        this.projectId = projectId;
        this.name = name;
        this.email = email;
        this.projectRole = projectRole;
        this.approvalStatus = "PENDING";
    }

    public static Approval newInstance(UUID id, UUID approverId, UUID projectId, String name, String email, String projectRole){
        return new Approval(id, approverId, projectId, name, email, projectRole);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Approval other = (Approval) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Approval{" +
                "teamId=" + id +
                ", projectId=" + projectId +
                ", approverId=" + approverId +
                ", title='" + name + '\'' +
                ", email='" + email + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", projectRole='" + projectRole + '\'' +
                ", decisionDate=" + decisionDate +
                ", decisionReason='" + decisionReason + '\'' +
                '}';
    }
}
