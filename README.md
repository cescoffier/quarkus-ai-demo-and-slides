# From Chatbot to Agentic: The Quarkus AI Story

A 55-minute conference talk delivered as a single Quarkus application that serves both the [reveal.js](https://revealjs.com/) slide deck and all live demos.

## Talk Structure

The presentation walks through six chapters covering enterprise AI with Quarkus:

1. **Hello, AI** -- AI Services, first integration
2. **Making It Real** -- Guardrails, RAG, context engineering, memory
3. **Agents That Act** -- Tools, MCP, tool approval
4. **Coordinating Agents** -- A2A, agentic loop, multi-agent patterns (`@SequenceAgent`)
5. **Making It Durable** -- Quarkus Flow, event-driven workflows, human-in-the-loop
6. **Trust and Verify** -- Observability, auditing, testing, evaluation

## Live Demos

Each demo runs inside the presentation itself -- no terminal switching, no separate apps.

| Demo | What it shows |
|------|--------------|
| **Hello, AI** | Simple AI service chat via WebSocket |
| **Guardrails + RAG** | Input guardrails rejecting off-topic prompts, RAG-augmented answers from Acme Corp docs |
| **Tool-Using Agent** | AI calling tools (order lookup, cancellation), tool approval flow, audit log |
| **Multi-Agent** | `@SequenceAgent` pipeline: Planner -> Research Agent -> Writer Agent |
| **Durable Workflow** | Quarkus Flow refund workflow with AI analysis, Kafka-based human-in-the-loop approval |

## Tech Stack

- **Quarkus 3.34.5** with Dev Services (PostgreSQL, Redpanda/Kafka via Testcontainers)
- **Quarkus LangChain4j 1.9.0.CR2** -- AI Services, guardrails, RAG, tools, `@SequenceAgent`
- **Quarkus Flow 0.8.0** -- Durable workflow engine (CNCF Serverless Workflow spec)
- **OpenAI GPT-4o** via `quarkus-langchain4j-openai`
- **reveal.js 5.2.1** served as a static resource via mvnpm

## Prerequisites

- Java 21+
- Docker (for Dev Services)
- An OpenAI API key

## Running

```shell
export OPENAI_API_KEY=sk-...
./mvnw quarkus:dev
```

Open http://localhost:8080 to view the slides. All demos work in dev mode -- PostgreSQL and Kafka are started automatically by Dev Services.

## Project Layout

```
src/main/java/io/quarkus/presentation/
  ai/
    CustomerAssistant.java        # Main AI service (chat, guardrails, RAG, tools)
    TopicGuardrail.java           # Input guardrail for off-topic detection
    OrderTools.java               # AI tools: order status, cancellation
    AcmeRetrievalAugmentor.java   # RAG retrieval augmentor
    RagIngestion.java             # Loads product/policy docs into embedding store
    Planner.java                  # Multi-agent orchestrator
    TopicResearchWorkflow.java    # @SequenceAgent: ResearchAgent + WriterAgent
    ResearchAgent.java            # Research sub-agent
    WriterAgent.java              # Writer sub-agent
    workflow/
      RefundWorkflow.java         # Quarkus Flow workflow definition
      RefundAnalysisAgent.java    # AI agent for refund analysis
      RefundService.java          # Refund processing logic
      RefundBridge.java           # Kafka CloudEvent bridge for UI state
      RefundWorkflowTracker.java  # Workflow state tracker for polling UI
  rest/
    ChatWebSocket.java            # WebSocket endpoint for chat demo
    DemoResource.java             # REST endpoints for all demos
  model/
    Customer.java, Order.java     # JPA entities
  service/
    OrderService.java             # Order persistence

src/main/resources/
  META-INF/resources/
    slides.md                     # All slides (reveal.js markdown)
    index.html                    # reveal.js setup
    js/demos.js                   # Demo panel initialization and interaction
    css/theme.css                 # Slide theme with CSS Paint API worklets
```
