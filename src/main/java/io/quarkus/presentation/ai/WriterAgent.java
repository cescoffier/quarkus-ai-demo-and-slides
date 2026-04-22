package io.quarkus.presentation.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface WriterAgent {

    @SystemMessage("""
        You are a professional writer. Given research findings, write a concise summary report.
        Keep it to 2-3 paragraphs. Be clear and professional.
        """)
    String write(@UserMessage String researchFindings);
}
