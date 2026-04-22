package io.quarkus.presentation.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService
@SessionScoped
public interface CustomerAssistant {

    @SystemMessage("You are a helpful customer support agent for Acme Corp. Be concise and friendly.")
    @UserMessage("Customer question: {question}")
    String chat(String question);
}
