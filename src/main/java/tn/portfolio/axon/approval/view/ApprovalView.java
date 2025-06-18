package tn.portfolio.axon.approval.view;

import java.util.UUID;

public record ApprovalView(String approver, String approverEmail, String approverRole, String status, UUID projectId, String project) {
}
