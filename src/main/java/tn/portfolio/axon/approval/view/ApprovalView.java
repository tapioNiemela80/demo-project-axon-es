package tn.portfolio.axon.approval.view;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApprovalView(String approver, String approverEmail, String approverRole, String status,
                           LocalDateTime decisionDate, String decisionReason, UUID projectId, String project) {
}
