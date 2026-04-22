package io.quarkus.presentation.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface ResearchAgent {

    @SystemMessage("""
        You are a research assistant. Given a topic, provide 3-5 key facts or findings.
        Be factual and concise. Format as bullet points.
        """)
    String research(@UserMessage String topic);
}
