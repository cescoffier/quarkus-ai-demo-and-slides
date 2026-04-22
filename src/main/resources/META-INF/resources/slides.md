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
