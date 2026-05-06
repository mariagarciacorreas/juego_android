package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * OverlayPausa genera una capa visual que oscurece el fondo del mapa
 * y renderiza un menú minimalista indicando que el juego está en suspensión,
 * aplicando animaciones cíclicas en los textos de advertencia.
 */public class OverlayPausa {

     //estado y animación
    private boolean visible = false;
    private float tiempo = 0f;

    //pinceles
    private final Paint paintOscuro, paintTexto, paintSubtexto;

    /**
     * Constructor: Configura la paleta de colores y las tipografías para la pausa.
     */
    public OverlayPausa() {
        // Filtro de oscurecimiento global translúcido (Dimmer)
        paintOscuro = new Paint();
        paintOscuro.setColor(Color.argb(170, 0, 0, 0));

        // Título principal ("PAUSA") en color amarillo vibrante y gran tamaño
        paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTexto.setColor(Color.rgb(255, 235, 59));
        paintTexto.setTextSize(100f);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTextAlign(Paint.Align.CENTER);

        // Subtexto descriptivo grisáceo para guiar al jugador
        paintSubtexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSubtexto.setColor(Color.argb(200, 200, 200, 200));
        paintSubtexto.setTextSize(32f);
        paintSubtexto.setTypeface(Typeface.MONOSPACE);
        paintSubtexto.setTextAlign(Paint.Align.CENTER);
    }

    public void mostrar() {
        visible = true;
        tiempo = 0f;
    }
    public void ocultar() {
        visible = false;
    }
    public boolean isVisible() {
        return visible;
    }

    /**
     * Incrementa de manera uniforme el factor temporal de la animación.
     * Este método es controlado por el hilo del juego a través del GestorHUD.
     */
    public void actualizar() {
        if (visible) tiempo += 0.05f;
    }

    /**
     * Renderiza la pantalla de pausa aplicando transformaciones dinámicas de matriz y alfa.
     */
    public void dibujar(Canvas canvas) {
        if (!visible) return;

        float w = canvas.getWidth(), h = canvas.getHeight();

        // Oscurece todo el lienzo del juego
        canvas.drawRect(0, 0, w, h, paintOscuro);

        // Animación del título principal (efecto latido)
        float esc = 1f + 0.03f * (float) Math.sin(tiempo);
        canvas.save(); //guarda el estado del canvas

        // Desplazar el origen físico del centro geométrico del texto para el escalado
        canvas.translate(w / 2f, h / 2f - 30);
        canvas.scale(esc, esc);

        // Dibujar el texto en el origen relativo (0,0)
        canvas.drawText("PAUSA", 0, 0, paintTexto);
        canvas.restore(); //restaurar canvas para liberar de escalados

        // Animación del subtexto (efecto parpadeo)
        paintSubtexto.setAlpha((int) Math.max(0, Math.min(255, 150 + 80 * Math.sin(tiempo))));
        // Dibujar la instrucción
        canvas.drawText("Pulsa ⏸ para reanudar", w / 2f, h / 2f + 80, paintSubtexto);
    }
}
