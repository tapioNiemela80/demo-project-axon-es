package tn.portfolio.axon.project.projection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :projectId")
    Optional<Project> findByIdWithTasks(@Param("projectId") UUID projectId);

    @Query("SELECT p FROM Project p JOIN p.tasks t WHERE t.id = :taskId")
    Optional<Project> findProjectByTaskId(@Param("taskId") UUID taskId);
}