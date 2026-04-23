package io.quarkus.presentation.ai.workflow;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class RefundWorkflowTracker {

    public record WorkflowStep(String name, String status, String detail, String timestamp) {}
    public record WorkflowState(String id, List<WorkflowStep> steps, boolean waitingForApproval, String recommendation) {}

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final CopyOnWriteArrayList<WorkflowStep> steps = new CopyOnWriteArrayList<>();
    private volatile boolean waitingForApproval;
    private volatile String recommendation;
    private volatile String instanceId;

    public void reset(String instanceId) {
        steps.clear();
        this.instanceId = instanceId;
        this.waitingForApproval = false;
        this.recommendation = null;
    }

    public void addStep(String name, String status, String detail) {
        steps.add(new WorkflowStep(name, status, detail, now()));
    }

    public void updateStep(String name, String status, String detail) {
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).name().equals(name)) {
                steps.set(i, new WorkflowStep(name, status, detail, now()));
                return;
            }
        }
    }

    public void setWaitingForApproval(boolean waiting) {
        this.waitingForApproval = waiting;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public WorkflowState getState() {
        return new WorkflowState(instanceId, List.copyOf(steps), waitingForApproval, recommendation);
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
