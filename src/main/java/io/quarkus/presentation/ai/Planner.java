package io.quarkus.presentation.ai;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Planner {

    public record PlanStep(String agent, String status, String detail) {}
    public record PlanResult(List<PlanStep> steps, String finalResult) {}

    @Inject
    TopicResearchWorkflow workflow;

    public PlanResult execute(String task) {
        TopicResearchWorkflow.ResearchReport result = workflow.process(task);

        List<PlanStep> steps = List.of(
                new PlanStep("Planner", "completed", "Decomposed task into: Research → Write"),
                new PlanStep("Research Agent", "completed", truncate(result.research())),
                new PlanStep("Writer Agent", "completed", "Report complete"));

        return new PlanResult(steps, result.report());
    }

    private static String truncate(String text) {
        if (text == null) return "";
        return text.length() > 120 ? text.substring(0, 120) + "..." : text;
    }
}
