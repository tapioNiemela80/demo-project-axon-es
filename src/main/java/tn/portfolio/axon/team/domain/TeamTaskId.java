package tn.portfolio.axon.team.domain;

import java.util.Objects;
import java.util.UUID;

public record TeamTaskId(UUID value) {
    public TeamTaskId {
        Objects.requireNonNull(value, "TeamTaskId value cannot be null");
    }
}
