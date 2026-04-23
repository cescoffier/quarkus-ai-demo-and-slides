registerPaint('sketchyEmphasis', class {
    static get inputProperties() {
        return ['--emphasis-color', '--emphasis-width'];
    }

    paint(ctx, size, props) {
        const color = props.get('--emphasis-color').toString().trim() || 'rgba(69, 149, 235, 0.35)';
        const lineWidth = parseFloat(props.get('--emphasis-width')) || 3;

        ctx.lineWidth = lineWidth;
        ctx.strokeStyle = color;
        ctx.lineCap = 'round';

        const getRandomX = () => (Math.random() > 0.5 ? 1 : -1) * Math.round(Math.random() * 1);
        const getRandomY = () => Math.round(Math.random() * 6) + 2;

        let xOld = -10;
        let yOld = size.height - getRandomY();
        const numLines = Math.round((size.width + 10) / 2);

        ctx.beginPath();
        ctx.moveTo(xOld, yOld);

        for (let i = 0; i < numLines; i++) {
            const y = i % 2 === 1 ? size.height - getRandomY() : getRandomY();
            const x = -10 + (i / numLines) * (size.width + 10) + (i % 2 === 1 ? 0 : 14) + getRandomX();
            ctx.moveTo(xOld, yOld);
            ctx.lineTo(x, y);
            xOld = x;
            yOld = y;
        }

        ctx.stroke();
    }
});
