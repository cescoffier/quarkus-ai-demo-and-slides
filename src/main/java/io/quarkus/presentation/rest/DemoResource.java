package io.quarkus.presentation.rest;

import io.quarkus.presentation.ai.AuditLog;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/demo")
public class DemoResource {

    @Inject
    AuditLog auditLog;

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
}
