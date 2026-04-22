package io.quarkus.presentation.ai;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class Planner {

    public record PlanStep(String agent, String status, String detail) {}
    public record PlanResult(List<PlanStep> steps, String finalResult) {}

    @Inject
    ResearchAgent researchAgent;

    @Inject
    WriterAgent writerAgent;

    private final List<PlanStep> currentSteps = Collections.synchronizedList(new ArrayList<>());
    private volatile String currentResult = null;

    public PlanResult execute(String task) {
        currentSteps.clear();
        currentResult = null;

        currentSteps.add(new PlanStep("Planner", "completed", "Decomposed task into: Research → Write"));

        currentSteps.add(new PlanStep("Research Agent", "active", "Researching: " + task));
        String research = researchAgent.research(task);
        currentSteps.set(1, new PlanStep("Research Agent", "completed", "Research complete"));

        currentSteps.add(new PlanStep("Writer Agent", "active", "Writing summary from research findings"));
        String report = writerAgent.write("Task: " + task + "\n\nResearch findings:\n" + research);
        currentSteps.set(2, new PlanStep("Writer Agent", "completed", "Report complete"));

        currentResult = report;
        return new PlanResult(List.copyOf(currentSteps), report);
    }

    public List<PlanStep> getCurrentSteps() {
        return List.copyOf(currentSteps);
    }

    public String getCurrentResult() {
        return currentResult;
    }
}
