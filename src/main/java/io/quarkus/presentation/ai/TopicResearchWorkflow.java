package io.quarkus.presentation.ai;

import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;

public interface TopicResearchWorkflow {

    record ResearchReport(String research, String report) {}

    @SequenceAgent(
            outputKey = "researchReport",
            subAgents = {ResearchAgent.class, WriterAgent.class})
    ResearchReport process(String topic);

    @Output
    static ResearchReport output(String research, String report) {
        return new ResearchReport(research, report);
    }
}
