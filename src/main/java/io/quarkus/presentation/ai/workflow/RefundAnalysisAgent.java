package io.quarkus.presentation.ai.workflow;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface RefundAnalysisAgent {

    @SystemMessage("""
            You are a refund request analyst for Acme Corp.
            Analyze the refund request and provide a recommendation.
            Consider the reason, check if it aligns with Acme's return policy
            (30-day window, product defects, wrong items shipped).
            Be concise — 2-3 sentences max.
            """)
    @UserMessage("""
            Analyze this refund request:
            - Order ID: #{request.orderId}
            - Reason: {request.reason}
            """)
    RefundAnalysis analyze(@MemoryId String memoryId, @V("request") RefundRequest request);
}
