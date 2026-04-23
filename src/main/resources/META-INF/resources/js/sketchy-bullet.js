registerPaint('sketchyBullet', class {
    static get inputProperties() {
        return ['--bullet-color', '--bullet-size'];
    }

    paint(ctx, size, props) {
        const color = props.get('--bullet-color').toString().trim() || '#4595EB';
        const bulletSize = parseFloat(props.get('--bullet-size')) || 5;

        const cx = size.width / 2;
        const cy = size.height / 2;

        ctx.fillStyle = color;
        ctx.beginPath();
        const fillPoints = 10;
        for (let i = 0; i <= fillPoints; i++) {
            const angle = (i / fillPoints) * Math.PI * 2;
            const r = bulletSize + (Math.random() - 0.5) * 2;
            const x = cx + Math.cos(angle) * r;
            const y = cy + Math.sin(angle) * r;
            if (i === 0) ctx.moveTo(x, y);
            else ctx.lineTo(x, y);
        }
        ctx.closePath();
        ctx.fill();

        ctx.strokeStyle = color;
        ctx.lineWidth = 1.5;
        ctx.lineCap = 'round';

        for (let pass = 0; pass < 3; pass++) {
            ctx.beginPath();
            const points = 8;
            for (let i = 0; i <= points; i++) {
                const angle = (i / points) * Math.PI * 2;
                const r = bulletSize + (Math.random() - 0.5) * 3;
                const x = cx + Math.cos(angle) * r;
                const y = cy + Math.sin(angle) * r;
                if (i === 0) {
                    ctx.moveTo(x, y);
                } else {
                    const prevAngle = ((i - 0.5) / points) * Math.PI * 2;
                    const cpR = bulletSize + (Math.random() - 0.5) * 3.5;
                    const cpx = cx + Math.cos(prevAngle) * cpR;
                    const cpy = cy + Math.sin(prevAngle) * cpR;
                    ctx.quadraticCurveTo(cpx, cpy, x, y);
                }
            }
            ctx.closePath();
            ctx.stroke();
        }
    }
});
