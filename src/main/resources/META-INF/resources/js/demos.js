document.addEventListener('DOMContentLoaded', () => {
    initChatPanels();
});

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
