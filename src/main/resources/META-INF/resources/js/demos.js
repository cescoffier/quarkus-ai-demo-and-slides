document.addEventListener('DOMContentLoaded', () => {
    initAllDemos();
});

function initAllDemos() {
    initChatPanels();
    initAuditPanels();
    initMultiAgentPanels();
}

function initChatPanels() {
    document.querySelectorAll('.demo-chat').forEach(panel => {
        if (panel.dataset.initialized) return;
        panel.dataset.initialized = 'true';

        const input = panel.querySelector('.chat-input');
        const sendBtn = panel.querySelector('.chat-send');
        const messages = panel.querySelector('.chat-messages');
        const endpoint = panel.dataset.endpoint || '/api/chat';

        async function sendMessage() {
            const text = input.value.trim();
            if (!text) return;

            appendMessage(messages, text, 'user');
            input.value = '';

            try {
                const res = await fetch(endpoint, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ message: text })
                });
                const data = await res.json();
                if (data.blocked) {
                    appendMessage(messages, data.reply, 'blocked');
                } else {
                    appendMessage(messages, data.reply, 'assistant');
                }
            } catch (e) {
                appendMessage(messages, 'Error: ' + e.message, 'blocked');
            }
        }

        sendBtn.addEventListener('click', sendMessage);
        input.addEventListener('keydown', e => {
            if (e.key === 'Enter') sendMessage();
        });
    });
}

function appendMessage(container, text, type) {
    const div = document.createElement('div');
    div.className = 'chat-message ' + type;
    div.textContent = text;
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
}

function initMultiAgentPanels() {
    document.querySelectorAll('.demo-multi-agent').forEach(panel => {
        if (panel.dataset.initialized) return;
        panel.dataset.initialized = 'true';

        const input = panel.querySelector('.task-input');
        const sendBtn = panel.querySelector('.task-send');
        const stepsDiv = panel.querySelector('.agent-steps');
        const resultDiv = panel.querySelector('.agent-result');

        async function submitTask() {
            const text = input.value.trim();
            if (!text) return;

            stepsDiv.innerHTML = '<div class="workflow-step active"><span class="step-icon">&#x23F3;</span> Processing...</div>';
            resultDiv.textContent = '';
            sendBtn.disabled = true;

            try {
                const res = await fetch('/api/demo/multi-agent', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ task: text })
                });
                const data = await res.json();

                stepsDiv.innerHTML = data.steps.map(s =>
                    `<div class="workflow-step ${s.status}">
                        <span class="step-icon">${s.status === 'completed' ? '&#x2713;' : '&#x23F3;'}</span>
                        <strong>${s.agent}</strong>: ${s.detail}
                    </div>`
                ).join('');

                resultDiv.textContent = data.finalResult;
            } catch (e) {
                resultDiv.textContent = 'Error: ' + e.message;
            }
            sendBtn.disabled = false;
        }

        sendBtn.addEventListener('click', submitTask);
        input.addEventListener('keydown', e => {
            if (e.key === 'Enter') submitTask();
        });
    });
}

function initAuditPanels() {
    document.querySelectorAll('.demo-audit-log').forEach(panel => {
        if (panel.dataset.initialized) return;
        panel.dataset.initialized = 'true';

        setInterval(async () => {
            try {
                const res = await fetch('/api/demo/audit');
                const entries = await res.json();
                panel.innerHTML = entries.map(e =>
                    `<div class="audit-entry">
                        <span style="color:#888">[${e.timestamp}]</span>
                        <span class="tool-name">${e.toolName}</span>(${e.parameters})
                        ${e.result ? `→ <span class="${e.result.includes('APPROVAL') || e.result.includes('NOT FOUND') ? 'tool-blocked' : 'tool-result'}">${e.result}</span>` : ''}
                    </div>`
                ).join('');
                panel.scrollTop = panel.scrollHeight;
            } catch (e) { /* ignore */ }
        }, 1000);
    });
}
