package io.quarkus.presentation.ai;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class WorkflowSimulator {

    public record WorkflowStep(String name, String status, String detail, String timestamp) {}
    public record WorkflowState(String id, List<WorkflowStep> steps, boolean waitingForApproval, String recommendation) {}

    @Inject
    CustomerAssistant assistant;

    private final List<WorkflowStep> steps = new ArrayList<>();
    private volatile boolean waitingForApproval = false;
    private volatile String recommendation = null;
    private volatile String workflowId = null;

    public WorkflowState startRefundWorkflow(long orderId, String reason) {
        steps.clear();
        waitingForApproval = false;
        workflowId = "WF-" + System.currentTimeMillis() % 10000;

        addStep("Receive Request", "completed", "Refund request for order #" + orderId + ": " + reason);
        addStep("AI Analysis", "active", "Analyzing request...");

        String analysis = assistant.chat(
            "A customer wants a refund for order #" + orderId + ". Reason: " + reason +
            ". Analyze this request and recommend whether to approve or deny. Be brief.");
        updateLastStep("AI Analysis", "completed", "Recommendation: " + analysis);
        recommendation = analysis;

        addStep("Manager Approval", "waiting", "Awaiting manager decision");
        waitingForApproval = true;

        return getState();
    }

    public WorkflowState approve() {
        if (!waitingForApproval) return getState();

        updateStep("Manager Approval", "completed", "Approved by manager");
        waitingForApproval = false;

        addStep("Process Refund", "completed", "Refund processed successfully");
        addStep("Send Confirmation", "completed", "Customer notified via email");

        return getState();
    }

    public WorkflowState reject() {
        if (!waitingForApproval) return getState();

        updateStep("Manager Approval", "completed", "Rejected by manager");
        waitingForApproval = false;

        addStep("Notify Customer", "completed", "Customer notified of rejection");

        return getState();
    }

    public WorkflowState getState() {
        return new WorkflowState(workflowId, List.copyOf(steps), waitingForApproval, recommendation);
    }

    private void addStep(String name, String status, String detail) {
        steps.add(new WorkflowStep(name, status, detail, now()));
    }

    private void updateLastStep(String name, String status, String detail) {
        if (!steps.isEmpty()) {
            steps.set(steps.size() - 1, new WorkflowStep(name, status, detail, now()));
        }
    }

    private void updateStep(String name, String status, String detail) {
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).name().equals(name)) {
                steps.set(i, new WorkflowStep(name, status, detail, now()));
                return;
            }
        }
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
