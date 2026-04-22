package io.quarkus.presentation.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService
@SessionScoped
public interface CustomerAssistant {

    @SystemMessage("""
        You are a helpful customer support agent for Acme Corp.
        Be concise and friendly. Answer questions about Acme products, orders, and policies.
        When you use information from the provided context, mention the source naturally.
        """)
    @InputGuardrails(TopicGuardrail.class)
    String chat(@UserMessage String question);
}
