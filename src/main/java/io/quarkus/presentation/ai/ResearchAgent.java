package io.quarkus.presentation.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ResearchAgent {

    @SystemMessage("""
        You are a research assistant. Given a topic, provide 3-5 key facts or findings.
        Be factual and concise. Format as bullet points.
        """)
    @UserMessage("{topic}")
    @Agent(outputKey = "research",
            description = "Research specialist. Gathers key facts and findings about a topic.")
    String research(String topic);
}
