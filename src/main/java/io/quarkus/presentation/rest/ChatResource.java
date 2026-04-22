package io.quarkus.presentation.rest;

import io.quarkus.presentation.ai.CustomerAssistant;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

@Path("/api/chat")
public class ChatResource {

    @Inject
    CustomerAssistant assistant;

    public record ChatRequest(String message) {}
    public record ChatResponse(String reply, boolean blocked) {}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChatResponse chat(ChatRequest request) {
        String reply = assistant.chat(request.message());
        return new ChatResponse(reply, false);
    }
}
