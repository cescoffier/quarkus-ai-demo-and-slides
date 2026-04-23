package io.quarkus.presentation.ai.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RefundBridge {

    private static final Logger LOG = Logger.getLogger(RefundBridge.class);
    private static final JsonFormat CE_JSON = (JsonFormat) EventFormatProvider.getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE);
    private static final String APPROVAL_REQUIRED = "refund.approval.required";

    @Inject
    RefundWorkflowTracker tracker;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("flow-out-incoming")
    public void onFlowOut(byte[] record) {
        try {
            CloudEvent ce = CE_JSON.deserialize(record);
            if (ce == null || !APPROVAL_REQUIRED.equals(ce.getType())) {
                return;
            }

            LOG.infof("Received approval-required event (instance: %s)", ce.getExtension("flowinstanceid"));

            if (ce.getData() != null) {
                RefundAnalysis analysis = objectMapper.readValue(ce.getData().toBytes(), RefundAnalysis.class);
                tracker.updateStep("AI Analysis", "completed", "Recommendation: " + analysis.recommendation());
                tracker.setRecommendation(analysis.recommendation());
            } else {
                tracker.updateStep("AI Analysis", "completed", "Analysis complete");
            }

            tracker.addStep("Manager Approval", "waiting", "Awaiting manager decision");
            tracker.setWaitingForApproval(true);
        } catch (Exception e) {
            LOG.error("Failed to process flow-out event", e);
        }
    }
}
