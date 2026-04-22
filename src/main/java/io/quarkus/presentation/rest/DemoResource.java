package io.quarkus.presentation.rest;

import io.quarkus.presentation.ai.AuditLog;
import io.quarkus.presentation.ai.Planner;
import io.quarkus.presentation.ai.WorkflowSimulator;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/demo")
public class DemoResource {

    public record TaskRequest(String task) {}
    public record RefundRequest(long orderId, String reason) {}

    @Inject
    AuditLog auditLog;

    @Inject
    Planner planner;

    @Inject
    WorkflowSimulator workflow;

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

    @GET
    @Path("/multi-agent/status")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Planner.PlanStep> getMultiAgentStatus() {
        return planner.getCurrentSteps();
    }

    @POST
    @Path("/workflow/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkflowSimulator.WorkflowState startWorkflow(RefundRequest request) {
        return workflow.startRefundWorkflow(request.orderId(), request.reason());
    }

    @POST
    @Path("/workflow/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkflowSimulator.WorkflowState approveWorkflow() {
        return workflow.approve();
    }

    @POST
    @Path("/workflow/reject")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkflowSimulator.WorkflowState rejectWorkflow() {
        return workflow.reject();
    }

    @GET
    @Path("/workflow/status")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkflowSimulator.WorkflowState getWorkflowStatus() {
        return workflow.getState();
    }
}
