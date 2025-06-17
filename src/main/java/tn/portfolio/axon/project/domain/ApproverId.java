package tn.portfolio.axon.project.domain;

import java.util.Objects;
import java.util.UUID;

public record ApproverId(UUID value) {
    public ApproverId {
        Objects.requireNonNull(value, "ApproverId value cannot be null");
    }
}
