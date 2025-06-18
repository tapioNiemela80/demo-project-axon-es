package tn.portfolio.axon.approval.view;

import org.springframework.stereotype.Service;
import tn.portfolio.axon.common.service.EntityManagerUtils;
import tn.portfolio.axon.common.service.EntityRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public
class ApprovalViewService {
    private final EntityManagerUtils entityManagerUtils;

    ApprovalViewService(EntityManagerUtils entityManagerUtils) {
        this.entityManagerUtils = entityManagerUtils;
    }

    public List<ApprovalView> findApprovalStatusOfProject(UUID projectId) {
        String sql = """
                SELECT a.name, a.email, a.project_role, a.approval_status as approval_status,p.id as project_id, p.name as project_name
                FROM approvals a
                LEFT JOIN projects p on a.project_id = p.id
                WHERE p.id = :projectId
                """;
        return entityManagerUtils.find(sql, Map.of("projectId", projectId), mapper());
    }

    private Function<EntityRecord, ApprovalView> mapper() {
        return r -> new ApprovalView(r.getString("name"), r.getString("email"),
                r.getString("project_role"), r.getString("approval_status"),
                r.getUUID("project_id"), r.getString("project_name"));
    }

}
