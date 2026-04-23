document.addEventListener('DOMContentLoaded', () => {
    const slidesContainer = document.querySelector('.reveal .slides');
    if (slidesContainer) {
        new MutationObserver(() => initAllDemos()).observe(slidesContainer, {
            childList: true, subtree: true
        });
    }
    initAllDemos();
});

function initAllDemos() {
    initChatPanels();
    initAuditPanels();
    initMultiAgentPanels();
    initWorkflowPanels();
}

function initChatPanels() {
    document.querySelectorAll('.demo-chat').forEach(panel => {
        if (panel.dataset.initialized) return;
        panel.dataset.initialized = 'true';

        const input = panel.querySelector('.chat-input');
        const sendBtn = panel.querySelector('.chat-send');
        const messages = panel.querySelector('.chat-messages');

        const wsUrl = 'ws://' + location.host + '/ws/chat';
        const ws = new WebSocket(wsUrl);

        ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            if (data.blocked) {
                appendMessage(messages, data.reply, 'blocked');
            } else {
                appendMessage(messages, data.reply, 'assistant');
            }
            sendBtn.disabled = false;
        };

        ws.onerror = () => {
            appendMessage(messages, 'WebSocket connection error', 'blocked');
            sendBtn.disabled = false;
        };

        function sendMessage() {
            const text = input.value.trim();
            if (!text || ws.readyState !== WebSocket.OPEN) return;

            appendMessage(messages, text, 'user');
            input.value = '';
            sendBtn.disabled = true;
            ws.send(text);
        }

        sendBtn.addEventListener('click', sendMessage);
        input.addEventListener('keydown', e => {
            if (e.key === 'Enter') sendMessage();
        });
    });
}

function simpleMarkdown(text) {
    return text
        .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.+?)\*/g, '<em>$1</em>')
        .replace(/`(.+?)`/g, '<code>$1</code>')
        .replace(/\n/g, '<br>');
}

function appendMessage(container, text, type) {
    const div = document.createElement('div');
    div.className = 'chat-message ' + type;
    div.innerHTML = simpleMarkdown(text);
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
        const pipelineNodes = panel.querySelectorAll('.pipeline-node');

        function resetPipeline() {
            pipelineNodes.forEach(n => { n.classList.remove('active', 'done'); });
            stepsDiv.innerHTML = '';
            resultDiv.innerHTML = '';
            resultDiv.classList.remove('visible');
        }

        function animatePipeline(stepIndex) {
            pipelineNodes.forEach((n, i) => {
                n.classList.remove('active', 'done');
                if (i < stepIndex) n.classList.add('done');
                else if (i === stepIndex) n.classList.add('active');
            });
        }

        async function submitTask() {
            const text = input.value.trim();
            if (!text) return;

            resetPipeline();
            animatePipeline(0);
            stepsDiv.innerHTML = '<div class="workflow-step active"><span class="step-icon">&#x23F3;</span> Agents working...</div>';
            sendBtn.disabled = true;

            try {
                const res = await fetch('/api/demo/multi-agent', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ task: text })
                });
                const data = await res.json();

                for (let i = 0; i < data.steps.length; i++) {
                    animatePipeline(i);
                    await new Promise(r => setTimeout(r, 400));
                }
                pipelineNodes.forEach(n => { n.classList.remove('active'); n.classList.add('done'); });

                stepsDiv.innerHTML = data.steps.map(s =>
                    `<div class="workflow-step completed">
                        <span class="step-icon">&#x2713;</span>
                        <strong>${s.agent}</strong>: ${s.detail}
                    </div>`
                ).join('');

                resultDiv.innerHTML = simpleMarkdown(data.finalResult);
                resultDiv.classList.add('visible');
            } catch (e) {
                resetPipeline();
                resultDiv.innerHTML = 'Error: ' + e.message;
                resultDiv.classList.add('visible');
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

function initWorkflowPanels() {
    document.querySelectorAll('.demo-workflow').forEach(panel => {
        if (panel.dataset.initialized) return;
        panel.dataset.initialized = 'true';

        const triggerBtn = panel.querySelector('.workflow-trigger');
        const stepsDiv = panel.querySelector('.workflow-steps');
        const approvalDiv = panel.querySelector('.approval-buttons');
        const approveBtn = panel.querySelector('.btn-approve');
        const rejectBtn = panel.querySelector('.btn-reject');
        let pollInterval = null;

        function renderState(state) {
            if (!state || !state.steps || state.steps.length === 0) {
                return;
            }
            stepsDiv.innerHTML = state.steps.map(s => {
                const icon = s.status === 'completed' ? '✓' :
                             s.status === 'waiting' ? '⏸' :
                             s.status === 'active' ? '⏳' : '○';
                return `<div class="workflow-step ${s.status}">
                    <span class="step-icon">${icon}</span>
                    <div>
                        <strong>${s.name}</strong>
                        <div style="font-size:0.85em;color:#666">${s.detail}</div>
                        <div style="font-size:0.75em;color:#999">${s.timestamp}</div>
                    </div>
                </div>`;
            }).join('');

            approvalDiv.style.display = state.waitingForApproval ? 'flex' : 'none';

            const allDone = state.steps.length > 0 &&
                state.steps.every(s => s.status === 'completed') &&
                !state.waitingForApproval;
            if (allDone && pollInterval) {
                clearInterval(pollInterval);
                pollInterval = null;
                triggerBtn.disabled = false;
            }
        }

        function startPolling() {
            if (pollInterval) clearInterval(pollInterval);
            pollInterval = setInterval(async () => {
                try {
                    const res = await fetch('/api/demo/workflow/status');
                    renderState(await res.json());
                } catch (e) { /* ignore */ }
            }, 500);
        }

        triggerBtn.addEventListener('click', async () => {
            triggerBtn.disabled = true;
            approvalDiv.style.display = 'none';
            stepsDiv.innerHTML = '<div class="workflow-step active"><span class="step-icon">⏳</span> Starting workflow...</div>';
            try {
                await fetch('/api/demo/workflow/start', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ orderId: 1238, reason: 'Product not as described' })
                });
                startPolling();
            } catch (e) {
                stepsDiv.innerHTML = '<div class="workflow-step">Error: ' + e.message + '</div>';
                triggerBtn.disabled = false;
            }
        });

        approveBtn.addEventListener('click', async () => {
            approvalDiv.style.display = 'none';
            await fetch('/api/demo/workflow/approve', { method: 'POST' });
        });

        rejectBtn.addEventListener('click', async () => {
            approvalDiv.style.display = 'none';
            await fetch('/api/demo/workflow/reject', { method: 'POST' });
        });
    });
}
