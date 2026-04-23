class RoughLine {
    constructor(roughness) {
        this.roughness = roughness;
        this.bowing = 1;
        this.maxRandomnessOffset = 1;
    }

    getOffset(min, max) {
        return this.roughness * ((Math.random() * (max - min)) + min);
    }

    getOffsetStart(min, max) {
        return this.roughness * 2 * ((Math.random() * (max - min)) + min);
    }

    drawLine(ctx, x1, y1, x2, y2) {
        const lengthSq = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        let offset = this.maxRandomnessOffset;
        if (offset * offset * 100 > lengthSq) offset = Math.sqrt(lengthSq) / 10;

        const divergePoint = 0.2 + Math.random() * 0.2;
        let midDispX = this.bowing * this.maxRandomnessOffset * (y2 - y1) / 200;
        let midDispY = this.bowing * this.maxRandomnessOffset * (x1 - x2) / 200;
        midDispX = this.getOffset(-midDispX, midDispX);
        midDispY = this.getOffset(-midDispY, midDispY);

        ctx.beginPath();
        ctx.moveTo(
            x1 + this.getOffsetStart(-offset, offset),
            y1 + this.getOffsetStart(-offset, offset)
        );
        ctx.bezierCurveTo(
            midDispX + x1 + (x2 - x1) * divergePoint + this.getOffset(-offset, offset),
            midDispY + y1 + (y2 - y1) * divergePoint + this.getOffset(-offset, offset),
            midDispX + x1 + 2 * (x2 - x1) * divergePoint + this.getOffset(-offset, offset),
            midDispY + y1 + 2 * (y2 - y1) * divergePoint + this.getOffset(-offset, offset),
            x2 + this.getOffsetStart(-offset, offset),
            y2 + this.getOffsetStart(-offset, offset)
        );
        ctx.stroke();
    }
}

registerPaint('sketchyBox', class {
    static get inputProperties() {
        return ['--box-color', '--box-fill', '--box-width', '--box-roughness'];
    }

    paint(ctx, geom, props) {
        const stroke = props.get('--box-color').toString().trim() || '#FF024A';
        const fill = props.get('--box-fill').toString().trim() || '';
        const strokeWidth = parseFloat(props.get('--box-width')) || 2;
        const roughness = parseFloat(props.get('--box-roughness')) || 3;

        const padding = 12;
        const left = padding;
        const right = geom.width - padding;
        const top = padding;
        const bottom = geom.height - padding;

        const rough = new RoughLine(roughness);

        if (fill) {
            ctx.save();
            ctx.fillStyle = fill;
            const o = rough.maxRandomnessOffset;
            ctx.beginPath();
            ctx.moveTo(left + rough.getOffset(-o, o), top + rough.getOffset(-o, o));
            ctx.lineTo(right + rough.getOffset(-o, o), top + rough.getOffset(-o, o));
            ctx.lineTo(right + rough.getOffset(-o, o), bottom + rough.getOffset(-o, o));
            ctx.lineTo(left + rough.getOffset(-o, o), bottom + rough.getOffset(-o, o));
            ctx.fill();
            ctx.restore();
        }

        ctx.strokeStyle = stroke;
        ctx.lineWidth = strokeWidth;
        ctx.lineCap = 'round';

        for (let i = 0; i < 4; i++) {
            rough.drawLine(ctx, left, top, right, top);
            rough.drawLine(ctx, right, top, right, bottom);
            rough.drawLine(ctx, right, bottom, left, bottom);
            rough.drawLine(ctx, left, bottom, left, top);
        }
    }
});
