package io.quarkus.presentation.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.presentation.ai.AuditLog;
import io.quarkus.presentation.ai.Planner;
import io.quarkus.presentation.ai.workflow.RefundDecision;
import io.quarkus.presentation.ai.workflow.RefundRequest;
import io.quarkus.presentation.ai.workflow.RefundWorkflow;
import io.quarkus.presentation.ai.workflow.RefundWorkflowTracker;
import io.serverlessworkflow.impl.WorkflowInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/api/demo")
public class DemoResource {

    public record TaskRequest(String task) {}

    private static final JsonFormat CE_JSON = (JsonFormat) EventFormatProvider.getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE);

    @Inject
    AuditLog auditLog;

    @Inject
    Planner planner;

    @Inject
    RefundWorkflow workflow;

    @Inject
    RefundWorkflowTracker tracker;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("flow-in-outgoing")
    Emitter<byte[]> flowIn;

    @GET
    @Path("/audit")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AuditLog.Entry> getAuditLog() {
        return auditLog.getEntries();
    }

    @DELETE
    @Path("/audit")
    public void clearAuditLog() {
        auditLog.clear();
    }

    @POST
    @Path("/multi-agent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Planner.PlanResult executeMultiAgent(TaskRequest request) {
        return planner.execute(request.task());
    }

    @POST
    @Path("/workflow/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startWorkflow(RefundRequest request) {
        WorkflowInstance instance = workflow.instance(request);
        tracker.reset(instance.id());
        tracker.addStep("Receive Request", "completed",
                "Refund request for order #" + request.orderId() + ": " + request.reason());
        tracker.addStep("AI Analysis", "active", "Analyzing request...");
        instance.start();
        return Response.accepted(
                Map.of("instanceId", instance.id())).build();
    }

    @POST
    @Path("/workflow/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveWorkflow() throws JsonProcessingException {
        RefundDecision decision = new RefundDecision(RefundDecision.Status.APPROVED, "Manager approved");
        sendDecisionEvent(decision);
        return Response.accepted().build();
    }

    @POST
    @Path("/workflow/reject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectWorkflow() throws JsonProcessingException {
        RefundDecision decision = new RefundDecision(RefundDecision.Status.REJECTED, "Manager rejected");
        sendDecisionEvent(decision);
        return Response.accepted().build();
    }

    @GET
    @Path("/workflow/status")
    @Produces(MediaType.APPLICATION_JSON)
    public RefundWorkflowTracker.WorkflowState getWorkflowStatus() {
        return tracker.getState();
    }

    private void sendDecisionEvent(RefundDecision decision) throws JsonProcessingException {
        byte[] body = objectMapper.writeValueAsBytes(decision);
        CloudEvent ce = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("api:/refund"))
                .withType("refund.decision.made")
                .withDataContentType("application/json")
                .withData(body)
                .build();
        flowIn.send(CE_JSON.serialize(ce));
    }
}
