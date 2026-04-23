package io.quarkus.presentation.ai.workflow;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;

public record RefundDecision(Status status, String notes) {

    public enum Status {
        APPROVED, REJECTED;

        @JsonCreator
        public static Status from(String v) {
            return v == null ? null : Status.valueOf(v.trim().toUpperCase(Locale.ROOT));
        }
    }
}
