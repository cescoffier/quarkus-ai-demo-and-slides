package io.quarkus.presentation.ai.workflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RefundService {

    private static final Logger LOG = Logger.getLogger(RefundService.class);

    @Inject
    RefundWorkflowTracker tracker;

    public RefundDecision processRefund(RefundDecision decision) {
        LOG.infof("Processing approved refund: %s", decision);
        tracker.updateStep("Manager Approval", "completed", "Approved by manager");
        tracker.setWaitingForApproval(false);
        tracker.addStep("Process Refund", "completed", "Refund processed — credit issued");
        tracker.addStep("Send Confirmation", "completed", "Customer notified via email");
        return decision;
    }

    public RefundDecision notifyRejection(RefundDecision decision) {
        LOG.infof("Refund rejected: %s", decision);
        tracker.updateStep("Manager Approval", "completed", "Rejected by manager");
        tracker.setWaitingForApproval(false);
        tracker.addStep("Notify Customer", "completed", "Customer notified of rejection");
        return decision;
    }
}
