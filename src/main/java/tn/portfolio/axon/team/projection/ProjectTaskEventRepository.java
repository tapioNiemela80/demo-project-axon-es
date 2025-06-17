package tn.portfolio.axon.team.projection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectTaskEventRepository extends JpaRepository<ProjectTaskEvent, UUID> {
}