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

---

<!-- .slide: data-background-color="#5B9BD5" -->

## Chapter 3: Agents That Act

What happens when AI can use your code?

---

## Tools: Giving AI Hands

```java
@Tool("Check the status of a customer order")
public String getOrderStatus(long orderId) {
    return orderService.findById(orderId).getStatus();
}
```

- Annotate a method with `@Tool` — exposed to the model
- The model decides *when* and *how* to call it
- Runs in your Quarkus app — full CDI context, transactions, security

<div class="key-message">Your Java services are already the tools. Just add @Tool.</div>

Note: The @Tool annotation turns any Java method into something the AI can call.

---

## Guardrails for Tools

```
Full autonomy <──────────────────> Full human control
  "Do anything"    "Do within bounds"    "Ask before acting"
```

- **Parameter validation** — reject invalid inputs
- **Scope restriction** — limit access
- **Approval flows** — flag high-risk actions
- **Audit trail** — every tool call logged

<div class="key-message">Enterprise AI means controlled AI.</div>

Note: Giving AI tools is powerful — and dangerous. Can it cancel orders? Transfer funds? You need control.

---

## MCP: A Standard Protocol

```java
@MCPTool(name = "order-status", description = "Get order status")
public OrderStatus getOrderStatus(long orderId) {
    return orderService.findById(orderId).getStatus();
}
```

- **Quarkus as MCP server** — expose your services as MCP tools
- **Quarkus as MCP client** — consume external capabilities
- One protocol, universal interoperability

<div class="key-message">MCP: the USB of AI. Quarkus speaks it natively.</div>

Note: MCP is becoming a universal standard for connecting agents to tools.

---

## Live Demo — Tool-Using Agent

<div class="demo-panel demo-chat" data-endpoint="/api/chat">
    <div class="chat-messages"></div>
    <div class="chat-input-row">
        <input type="text" class="chat-input" placeholder="Try: What's the status of order #1234?">
        <button class="chat-send">Send</button>
    </div>
    <div class="audit-log demo-audit-log"></div>
</div>

*Try: "What's the status of order #1234?" then "Cancel order #1234"*

Note: Watch what happens. I ask about an order — the agent calls getOrderStatus. Now I ask to cancel — the tool guardrail catches it.

---

<!-- .slide: data-background-color="#5B9BD5" -->

## Chapter 4: Coordinating Agents

From solo performer to orchestra.

---

## A2A: Agents Talking to Agents

- Each agent publishes an **agent card** — "here's what I can do"
- Agents discover, negotiate, and delegate
- **Federated AI** — no single monolithic agent
- **Team autonomy** — each team owns their agents

<div class="key-message">A2A: federated AI. Each team owns their agents.</div>

Note: In a real enterprise, one team owns orders, another owns inventory. You don't build one giant agent.

---

## The Planner: Orchestrating the Work

```
Fully scripted <─────────────────→ Fully autonomous
  "Follow this        "Here are the        "Figure it out"
   exact plan"          boundaries"
```

- **Fully scripted** — safe, predictable, brittle
- **Fully autonomous** — flexible, unpredictable
- **Constrained autonomy** — freedom *within guardrails*

<div class="key-message">How much freedom do you give the AI? Quarkus lets you choose.</div>

Note: This is the most important design decision in agentic AI.

---

## Coordination Patterns

| Pattern | How It Works | Example |
|---------|-------------|---------|
| **Sequential** | A → B → C | Intake → Review → Approval |
| **Fan-out** | Parallel, aggregated | Research from 5 sources |
| **Supervisor** | One oversees others | Manager monitors workers |
| **Debate** | Different perspectives | Legal + Business → Decision |

<div class="key-message">These patterns are your enterprise workflows.</div>

Note: These four patterns cover 80% of what you'll need.

---

## Live Demo — Multi-Agent

<div class="demo-panel demo-multi-agent">
    <div class="chat-input-row">
        <input type="text" class="task-input" placeholder="e.g., Research the benefits of Quarkus for AI applications">
        <button class="task-send chat-send">Submit</button>
    </div>
    <div class="agent-steps workflow-steps" style="margin-top: 1em;"></div>
    <div class="agent-result" style="margin-top: 1em; padding: 0.5em; white-space: pre-wrap;"></div>
</div>

Note: The planner breaks it down: research agent gathers information, writer agent synthesizes a report.
