package io.quarkus.presentation.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface WriterAgent {

    @SystemMessage("""
        You are a professional writer. Given a topic and research findings,
        write a concise summary report. Keep it to 2-3 paragraphs.
        Be clear and professional.
        """)
    @UserMessage("""
        Topic: {topic}

        Research findings:
        {research}
        """)
    @Agent(outputKey = "report",
            description = "Writer. Synthesizes research findings into a summary report.")
    String write(String topic, String research);
}
