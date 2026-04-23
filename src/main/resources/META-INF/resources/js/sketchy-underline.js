registerPaint('sketchyUnderline', class {
    static get inputProperties() {
        return ['--underline-color', '--underline-width', '--underline-spread', '--underline-lines'];
    }

    paint(ctx, size, props) {
        const color = props.get('--underline-color').toString().trim() || '#4595EB';
        const lineWidth = parseFloat(props.get('--underline-width')) || 1.2;
        const lineCount = parseInt(props.get('--underline-lines')) || 6;
        const spread = parseFloat(props.get('--underline-spread')) || 10;

        ctx.strokeStyle = color;
        ctx.lineCap = 'round';

        const getY = () => size.height - 5 - Math.random() * spread;

        for (let i = 0; i < lineCount; i++) {
            const x1 = Math.random() * 10;
            const y1 = getY();
            const x2 = size.width - Math.random() * 70;
            const y2 = getY();

            ctx.lineWidth = Math.random() * lineWidth + 1.5;
            ctx.beginPath();
            ctx.moveTo(x1, y1);

            const cpX = size.width / 2 + (Math.random() > 0.5 ? 1 : -1) * 0.15 * size.width;
            const cpY = (y1 + y2) / 2 + (Math.random() > 0.5 ? (Math.random() * 15 + 5) : -(Math.random() * 8));

            ctx.quadraticCurveTo(cpX, cpY, x2, y2);
            ctx.stroke();
        }
    }
});
