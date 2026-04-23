package io.quarkus.presentation.ai.workflow;

public record RefundRequest(long orderId, String reason) {
}
