package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

/**
 * Placeholder
 * Pantalla de resultado final: victoria o derrota.
 * Toque en pantalla → reinicia volviendo a EscenaInicio.
 */
public class EscenaFinal implements Escena {

    private final Context context;
    private final GestorEscenas gestorEscenas;
    private final boolean victoria;

    private final Paint paintTitulo;
    private final Paint paintSubtitulo;
    private final Paint paintPista;

    // Animación de aparición
    private float alpha = 0f;
    private float escalaTitulo = 0.5f;
    private float tiempoPulsacion = 0f;

    // Textos según resultado
    private final String[] lineasVictoria = {
            "¡¡CIBER FRANCO DERROTADO!!",
            "",
            "La Constitución ha sido recuperada.",
            "Los derechos del pueblo",
            "vuelven a estar protegidos.",
            "",
            "Tú lo has hecho posible, héroe.",
            "Ciudad Memópolis te lo agradece."
    };

    private final String[] lineasDerrota = {
            "HAS CAÍDO...",
            "",
            "Ciber Franco ríe sobre tu derrota.",
            "La Constitución permanece",
            "bajo su control digital.",
            "",
            "Pero la historia no ha terminado.",
            "El pueblo seguirá luchando."
    };

    public EscenaFinal(Context context, GestorEscenas gestorEscenas, boolean victoria) {
        this.context       = context;
        this.gestorEscenas = gestorEscenas;
        this.victoria      = victoria;

        paintTitulo = new Paint();
        paintTitulo.setTextSize(60);
        paintTitulo.setTypeface(Typeface.MONOSPACE);
        paintTitulo.setAntiAlias(false);
        paintTitulo.setColor(victoria ? Color.rgb(255, 235, 59) : Color.rgb(220, 50, 50));

        paintSubtitulo = new Paint();
        paintSubtitulo.setColor(Color.WHITE);
        paintSubtitulo.setTextSize(32);
        paintSubtitulo.setTypeface(Typeface.MONOSPACE);
        paintSubtitulo.setAntiAlias(false);

        paintPista = new Paint();
        paintPista.setColor(Color.GRAY);
        paintPista.setTextSize(28);
        paintPista.setTypeface(Typeface.MONOSPACE);
        paintPista.setAntiAlias(false);
    }

    @Override
    public void actualizar() {
        // Fade-in
        if (alpha < 255f) alpha = Math.min(255f, alpha + 4f);

        // Escala del título (crece hasta 1.0)
        if (escalaTitulo < 1f) escalaTitulo = Math.min(1f, escalaTitulo + 0.015f);

        // Pulsación de la pista
        tiempoPulsacion += 0.05f;
    }

    @Override
    public void renderizar(Canvas canvas) {

        if (canvas == null) return;

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        // Fondo
        canvas.drawColor(victoria ? Color.rgb(10, 30, 10) : Color.rgb(30, 10, 10));

        int a = (int) alpha;
        paintTitulo.setAlpha(a);
        paintSubtitulo.setAlpha(a);
        paintPista.setAlpha(a);

        String[] lineas = victoria ? lineasVictoria : lineasDerrota;
        float lineH = paintSubtitulo.getTextSize() + 16;

        // Calcular bloque total centrado verticalmente
        float altoBloque = lineH * lineas.length;
        float startY = h / 2f - altoBloque / 2f;

        // Primera línea: título grande con escala
        String titulo = lineas[0];
        float twTitulo = paintTitulo.measureText(titulo);

        canvas.save();
        canvas.translate(w / 2f, startY + paintTitulo.getTextSize() / 2f);
        canvas.scale(escalaTitulo, escalaTitulo);
        canvas.drawText(titulo, -twTitulo / 2f, 0, paintTitulo);
        canvas.restore();

        // Resto de líneas
        float y = startY + paintTitulo.getTextSize() + 20;
        for (int i = 1; i < lineas.length; i++) {
            float tw = paintSubtitulo.measureText(lineas[i]);
            canvas.drawText(lineas[i], w / 2f - tw / 2f, y, paintSubtitulo);
            y += lineH;
        }

        // Pista (abajo, pulsante)
        float escalaPista = 1f + 0.06f * (float) Math.sin(tiempoPulsacion);
        String textoPista = "▼ Pulsa para volver al inicio";
        float twPista = paintPista.measureText(textoPista);
        float pistaX = w / 2f;
        float pistaY = h - 60;

        canvas.save();
        canvas.translate(pistaX, pistaY);
        canvas.scale(escalaPista, escalaPista);
        canvas.drawText(textoPista, -twPista / 2f, 0, paintPista);
        canvas.restore();
    }

    @Override
    public void onTouch(float x, float y) {
        // Volver al inicio (reiniciar el juego completo)
        gestorEscenas.cambiarEscena(new EscenaInicio(context, gestorEscenas));
    }
}
