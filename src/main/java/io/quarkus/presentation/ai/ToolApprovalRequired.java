package io.quarkus.presentation.ai;

public class ToolApprovalRequired extends RuntimeException {
    public ToolApprovalRequired(String message) {
        super(message);
    }
}
