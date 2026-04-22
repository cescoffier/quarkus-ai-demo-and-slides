<!-- .slide: data-background-color="#FFFFFF" -->

# From Chatbot to Agentic

## The Quarkus AI Story

*A progressive journey through the Quarkus AI platform*

**Clement Escoffier** — Red Hat Distinguished Engineer, IBM — Quarkus Co-Lead

Note: Brief self-introduction. In the next 45 minutes, I'll take you on a journey — from a 10-line chatbot to full agentic coordination with human-in-the-loop.

---

<!-- .slide: data-background-color="#5B9BD5" -->

## Chapter 1: Hello, AI

Every AI journey starts with a conversation.

---

## Your First AI Service

A working AI chatbot in ~10 lines:

```java
@RegisterAiService
public interface MyAssistant {

    @SystemMessage("You are a helpful customer support agent for Acme Corp.")
    @UserMessage("Customer question: {question}")
    String chat(String question);
}
```

**10 lines of code. Any model. Hot reload your prompts.**

Note: Look at this. In Quarkus, it's a CDI interface. The @RegisterAiService annotation does the wiring.

---

## Live Demo — Hello, AI

<div class="demo-panel demo-chat" data-endpoint="/api/chat">
    <div class="chat-messages"></div>
    <div class="chat-input-row">
        <input type="text" class="chat-input" placeholder="Ask Acme Corp support anything...">
        <button class="chat-send">Send</button>
    </div>
</div>

Note: Let me show you this live. Notice I can change the system prompt and the response changes immediately — no restart.

---

<!-- .slide: data-background-color="#5B9BD5" -->

## Chapter 2: Making It Real

From demo to production — safety, knowledge, and structure.

---

## Guardrails: Trust but Verify

LLMs hallucinate, go off-topic, leak data, can be prompt-injected.

```java
@ApplicationScoped
public class TopicGuardrail implements InputGuardrail {
    @Override
    public InputGuardrailResult validate(UserMessage message) {
        if (isOffTopic(message.singleText())) {
            return failure("I can only help with Acme products.");
        }
        return success();
    }
}
```

- **Input guardrails** — validate *before* the model
- **Output guardrails** — validate *before* the user
- Pluggable, composable, CDI beans

<div class="key-message">Your AI, your rules. Guardrails are not optional in production.</div>

Note: In production, you need control. Guardrails let you intercept every message in and every response out.

---

## RAG: Your Data, Your AI

The model doesn't know your products, your policies, your data.

```
Question → Embed → Search vector store → Retrieve docs → Augment prompt → LLM → Response
```

- **Document loaders** — PDF, web, databases
- **Embedding models** — OpenAI, Ollama, local
- **Vector stores** — pgvector, Redis, Chroma
- **easy-rag** — just drop files in a folder

<div class="key-message">Enterprise AI gets its value from *your* data.</div>

Note: RAG bridges the gap — you embed your documents, and at query time the most relevant chunks are injected into the prompt.

---

## Context Engineering & Chat Memory

- Conversation history — automatically maintained per session
- Configurable window — sliding, token-based, summarizing
- **Context engineering** — what goes into the prompt matters more than which model you use

```java
@SessionScoped
public interface CustomerAssistant { ... }
// Session-scoped = conversation memory per user session
```

<div class="key-message">Context engineering is the real AI skill.</div>

Note: The prompt is everything. Which model you use matters far less than what you put in the context window.

---

## Live Demo — Guardrails + RAG

<div class="demo-panel demo-chat" data-endpoint="/api/chat">
    <div class="chat-messages"></div>
    <div class="chat-input-row">
        <input type="text" class="chat-input" placeholder="Ask about Acme products, or try going off-topic...">
        <button class="chat-send">Send</button>
    </div>
</div>

*Try: "What's the return policy?" then "What's the weather?"*

Note: The AI knows about our products because of RAG. But when I ask something off-topic, the input guardrail catches it before it even reaches the model.
