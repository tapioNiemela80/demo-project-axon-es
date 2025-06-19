package tn.portfolio.axon.approval.domain;

import java.util.Objects;
import java.util.UUID;

public record ApprovalId(UUID value) {
    public ApprovalId {
        Objects.requireNonNull(value, "ApprovalId value cannot be null");
    }
}
