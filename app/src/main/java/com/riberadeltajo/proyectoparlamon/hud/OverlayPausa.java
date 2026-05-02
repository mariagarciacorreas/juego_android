package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

//oscurecer pantalla y mostrar pausa
public class OverlayPausa {

    private boolean visible = false;
    private float tiempo = 0f;

    private final Paint paintOscuro, paintTexto, paintSubtexto;

    public OverlayPausa() {
        paintOscuro = new Paint();
        paintOscuro.setColor(Color.argb(170, 0, 0, 0));

        paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTexto.setColor(Color.rgb(255, 235, 59));
        paintTexto.setTextSize(100f);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTextAlign(Paint.Align.CENTER);

        paintSubtexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSubtexto.setColor(Color.argb(200, 200, 200, 200));
        paintSubtexto.setTextSize(32f);
        paintSubtexto.setTypeface(Typeface.MONOSPACE);
        paintSubtexto.setTextAlign(Paint.Align.CENTER);
    }

    public void mostrar() {
        visible = true; tiempo = 0f;
    }
    public void ocultar() {
        visible = false;
    }
    public boolean isVisible() {
        return visible;
    }

    public void actualizar() {
        if (visible) tiempo += 0.05f;
    }

    public void dibujar(Canvas canvas) {
        if (!visible) return;

        float w = canvas.getWidth(), h = canvas.getHeight();
        canvas.drawRect(0, 0, w, h, paintOscuro);

        float esc = 1f + 0.03f * (float) Math.sin(tiempo);
        canvas.save();
        canvas.translate(w / 2f, h / 2f - 30);
        canvas.scale(esc, esc);
        canvas.drawText("PAUSA", 0, 0, paintTexto);
        canvas.restore();

        paintSubtexto.setAlpha((int) Math.max(0, Math.min(255, 150 + 80 * Math.sin(tiempo))));
        canvas.drawText("Pulsa ⏸ para reanudar", w / 2f, h / 2f + 80, paintSubtexto);
    }
}
