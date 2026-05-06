package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * BotonHUD representa un componente de interfaz interactivo de geometría circular.
 * Es utilizado de forma genérica en las capas superiores de la UI (HUD) para renderizar
 * botones de control como Pausa o Ajustes, aislando la lógica de dibujado y colisión.
 */public class BotonHUD {

    //coordenada X del centro del botón en pantalla
    private final float cx;
    //coordenada Y del centro del botón en pantalla
    private final float cy;
    //radio del área física e interactiva del botón
    private final float radio;

    private final String joystic;
    private final Paint paintFondo;
    private final Paint paintBorde;
    private final Paint paintJoystic;

    public BotonHUD(float cx, float cy, float radio, String joystic) {

        this.cx = cx;
        this.cy = cy;
        this.radio = radio;
        this.joystic = joystic;

        // Fondo circular oscuro y semi-translúcido para permitir visibilidad del mapa por debajo
        paintFondo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFondo.setColor(Color.argb(160, 20, 20, 30));
        paintFondo.setStyle(Paint.Style.FILL);

        // Borde exterior definido con un color amarillo/dorado brillante (Estilo Cyberpunk/Retro)
        paintBorde = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBorde.setColor(Color.argb(200, 255, 235, 59));
        paintBorde.setStyle(Paint.Style.STROKE);
        paintBorde.setStrokeWidth(2.5f);

        // Configuración tipográfica del glifo central, escalando el tamaño de fuente respecto al radio
        paintJoystic = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintJoystic.setColor(Color.WHITE);
        paintJoystic.setTextSize(radio * 0.9f);
        paintJoystic.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Renderiza el botón en sus capas correspondientes (Fondo -> Borde -> Icono) sobre el lienzo.
     */
    public void dibujar(Canvas canvas){
        canvas.drawCircle(cx, cy, radio, paintFondo);
        canvas.drawCircle(cx, cy, radio, paintBorde);
        float offset = -(paintJoystic.ascent() + paintJoystic.descent()) / 2f;
        canvas.drawText(joystic, cx, cy + offset, paintJoystic);
    }

    /**
     * Evalúa si un toque táctil en pantalla (`x`, `y`) se encuentra dentro de la zona reactiva circular.
     * Utiliza la ecuación cartesiana de la distancia euclidiana simplificada para máxima optimización.
     * * @return true si la pulsación colisiona con el botón.
     */
    public boolean dentro(float x, float y){

        // Calcula las distancias en los ejes rectos respecto al centro geométrico
        float dx = x - cx, dy = y - cy;

        // Teorema de Pitágoras: (dx² + dy² <= radio²).
        // Evitamos calcular la raíz cuadrada (Math.sqrt) comparando directamente contra el radio al cuadrado,
        // lo que ahorra una cantidad masiva de ciclos de CPU en el motor de juego.
        return dx * dx + dy * dy <= radio * radio;
    }
}
