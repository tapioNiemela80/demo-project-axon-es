package tn.portfolio.axon.approval.projection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalRepository extends JpaRepository<Approval, UUID> {

    List<Approval> findByProjectId(UUID projectId);

    Optional<Approval> findByProjectIdAndApproverId(UUID projectId, UUID approverId);

}