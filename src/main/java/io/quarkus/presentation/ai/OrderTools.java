package io.quarkus.presentation.ai;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.presentation.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderTools {

    @Inject
    OrderService orderService;

    @Inject
    AuditLog auditLog;

    @Transactional
    @Tool("Get the current status of a customer order by order ID")
    public String getOrderStatus(long orderId) {
        auditLog.log("getOrderStatus", "orderId=" + orderId, null);
        return orderService.findById(orderId)
            .map(order -> "Order #%d: %s — %s ($%.2f) — Status: %s"
                .formatted(order.id, order.customer.name, order.product, order.amount, order.status))
            .orElseGet(() -> {
                auditLog.log("getOrderStatus", "orderId=" + orderId, "NOT FOUND");
                return "Order #" + orderId + " not found.";
            });
    }

    @Tool("Cancel a customer order by order ID. Use this when the customer requests cancellation.")
    public String cancelOrder(long orderId) {
        auditLog.log("cancelOrder", "orderId=" + orderId, "REQUIRES APPROVAL");
        throw new ToolApprovalRequired("Order cancellation requires manager approval. Order #" + orderId + " flagged for review.");
    }
}
