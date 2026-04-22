package io.quarkus.presentation.ai;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TopicGuardrail implements InputGuardrail {

    private static final List<String> OFF_TOPIC_KEYWORDS = List.of(
        "weather", "sports", "politics", "stock market", "recipe", "joke"
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText().toLowerCase();
        for (String keyword : OFF_TOPIC_KEYWORDS) {
            if (text.contains(keyword)) {
                return failure("I can only help with Acme Corp products and services. " +
                    "Please ask me about our products, orders, or policies.");
            }
        }
        return success();
    }
}
