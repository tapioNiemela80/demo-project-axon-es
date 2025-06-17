package tn.portfolio.axon.team.projection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.portfolio.axon.project.projection.Project;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    @Query("""
        SELECT t FROM Team t
        LEFT JOIN FETCH t.members
        LEFT JOIN FETCH t.tasks
        WHERE t.id = :teamId
    """)
    Optional<Team> findByIdWithMembersAndTasks(@Param("teamId") UUID teamId);

    @Query("""
    SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END
    FROM Team t
    JOIN t.tasks task
    WHERE task.projectTaskId = :projectTaskId
""")
    boolean existsByProjectTaskId(@Param("projectTaskId") UUID projectTaskId);
}