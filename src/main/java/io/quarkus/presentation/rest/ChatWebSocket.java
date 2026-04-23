package io.quarkus.presentation.rest;

import dev.langchain4j.guardrail.GuardrailException;
import io.quarkus.presentation.ai.CustomerAssistant;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;

@WebSocket(path = "/ws/chat")
public class ChatWebSocket {

    @Inject
    CustomerAssistant assistant;

    public record ChatResponse(String reply, boolean blocked) {}

    @OnTextMessage
    public ChatResponse onMessage(String message) {
        try {
            String reply = assistant.chat(message);
            return new ChatResponse(reply, false);
        } catch (GuardrailException e) {
            return new ChatResponse(e.getMessage(), true);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("requires manager approval")) {
                return new ChatResponse(e.getMessage(), true);
            }
            return new ChatResponse("An error occurred: " + e.getMessage(), true);
        }
    }
}
