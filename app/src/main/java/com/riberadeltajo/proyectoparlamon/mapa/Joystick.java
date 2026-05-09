package com.riberadeltajo.proyectoparlamon.mapa;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Joystick {
    private final float centroX;
    private final float centroY;
    private final float radioBase;
    private final float radioPalometa;

    private float palometaX;
    private float palometaY;

    // Vectores de salida normalizados (fuerza del movimiento)
    private float dx = 0f; // Factor de desplazamiento horizontal (Rango de -1.0 a 1.0)
    private float dy = 0f; // Factor de desplazamiento vertical (Rango de -1.0 a 1.0)

    private boolean activo = false;
    private int pointerIdActivo = -1;

    private final Paint paintBase;
    private final Paint paintBorde;
    private final Paint paintPalometa;


    public Joystick(float centroX, float centroY, float radioBase) {
        this.centroX = centroX;
        this.centroY = centroY;
        this.radioBase = radioBase;

        // El "pomo" ocupa el 42% del tamaño de la base
        this.radioPalometa = radioBase * 0.42f;

        // Inicializar palometa centrada en la base
        palometaX = centroX;
        palometaY = centroY;

        paintBase = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBase.setColor(Color.argb(70, 200, 200, 200));
        paintBase.setStyle(Paint.Style.FILL);

        paintBorde = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBorde.setColor(Color.argb(140, 255, 255, 255));
        paintBorde.setStyle(Paint.Style.STROKE);
        paintBorde.setStrokeWidth(3f);

        paintPalometa = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPalometa.setColor(Color.argb(200, 255, 235, 59));
        paintPalometa.setStyle(Paint.Style.FILL);
    }


    public boolean intentarActivar(int pointerId, float px, float py) {
        if (activo || !dentroDelAreaBase(px, py)) return false;
        pointerIdActivo = pointerId;
        activo = true;
        actualizar(px, py);
        return true;
    }


    public void mover(int pointerId, float px, float py) {
        if (!activo || pointerId != pointerIdActivo) return;
        actualizar(px, py);
    }


    public void soltar(int pointerId) {
        if (pointerId != pointerIdActivo) return;
        activo = false;
        pointerIdActivo = -1;

        // Efecto muelle: Resetea de golpe la palometa y anula los vectores de fuerza
        palometaX = centroX;
        palometaY = centroY;
        dx = 0f;
        dy = 0f;
    }

    public void dibujar(Canvas canvas) {
        canvas.drawCircle(centroX, centroY, radioBase, paintBase);
        canvas.drawCircle(centroX, centroY, radioBase, paintBorde);
        canvas.drawCircle(palometaX, palometaY, radioPalometa, paintPalometa);
    }

    public float getDx() { return dx; }
    public float getDy() { return dy; }
    public boolean isActivo() { return activo; }
    public int getPointerIdActivo() { return pointerIdActivo; }


    private void actualizar(float px, float py) {
        // 1. Hallar los componentes cartesianos del vector de desplazamiento (Offsets relativos al centro)
        float ox = px - centroX;
        float oy = py - centroY;
        // Teorema de Pitágoras: Calcula la distancia euclidiana en línea recta desde el centro al dedo
        float dist = (float) Math.sqrt(ox * ox + oy * oy);
        // Evita divisiones por cero si el dedo está exactamente sobre el centro geométrico
        if (dist == 0f) {
            dx = 0f; dy = 0f;
            return;
        }

        // 2. Acotación Radial (Clamping): Evita que la palometa se salga físicamente de la circunferencia base
        float dc = Math.min(dist, radioBase);

        // 3. Normalización del Vector: Hallamos el vector unitario director (valores entre -1.0 y 1.0)
        float nx = ox / dist, ny = oy / dist;

        // 4. Posicionamiento Visual: Coloca la palometa en la trayectoria del dedo respetando los límites
        palometaX = centroX + nx * dc;
        palometaY = centroY + ny * dc;

        // 5. Cálculo Analógico de Magnitud: Determina la intensidad de la velocidad (de 0.0 en el centro a 1.0 en los bordes)
        float mag = dc / radioBase;

        // Velocidad final ponderada lista para el PersonajeMapa
        dx = nx * mag;
        dy = ny * mag;

    }


    private boolean dentroDelAreaBase(float px, float py) {
        float ox = px - centroX, oy = py - centroY;
        return Math.sqrt(ox * ox + oy * oy) <= radioBase;
    }
}
