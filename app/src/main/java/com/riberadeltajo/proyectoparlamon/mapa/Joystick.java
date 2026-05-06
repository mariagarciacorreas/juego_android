package com.riberadeltajo.proyectoparlamon.mapa;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Joystick representa un pad analógico virtual circular en pantalla.
 * Proporciona un control de movimiento multidireccional fluido (360°) donde la velocidad
 * resultante del personaje es directamente proporcional a la distancia a la que el jugador
 * arrastra la palometa (el stick central) respecto a su base.
 */public class Joystick {
    private final float centroX;
    private final float centroY;
    private final float radioBase;
    private final float radioPalometa;

    private float palometaX;
    private float palometaY;

    // Vectores de salida normalizados (fuerza del movimiento)
    private float dx = 0f; // Factor de desplazamiento horizontal (Rango de -1.0 a 1.0)
    private float dy = 0f; // Factor de desplazamiento vertical (Rango de -1.0 a 1.0)

    // Gesión del foco multitouch
    private boolean activo = false;
    private int pointerIdActivo = -1;

    // Pinceles
    private final Paint paintBase;
    private final Paint paintBorde;
    private final Paint paintPalometa;

    /**
     * Constructor: Define la posición fija del pad en el HUD y calcula el tamaño proporcional
     * de la palometa interna con una paleta translúcida Cyberpunk/Amarilla.
     */
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

    /**
     * Intenta capturar el control cuando un dedo toca la pantalla (ACTION_DOWN o ACTION_POINTER_DOWN).
     * * @param pointerId ID del dedo que ha tocado la pantalla.
     * @param px Coordenada X del toque.
     * @param py Coordenada Y del toque.
     * @return true si el toque ocurrió dentro de la base y el joystick se activó con éxito.
     */
    public boolean intentarActivar(int pointerId, float px, float py) {
        if (activo || !dentroDelAreaBase(px, py)) return false;
        pointerIdActivo = pointerId;
        activo = true;
        actualizar(px, py);
        return true;
    }

    /**
     * Procesa el arrastre del dedo por la pantalla (ACTION_MOVE).
     * * @param pointerId ID del dedo que se está moviendo.
     */
    public void mover(int pointerId, float px, float py) {
        if (!activo || pointerId != pointerIdActivo) return;
        actualizar(px, py);
    }

    /**
     * Libera el control cuando el dedo se levanta de la pantalla (ACTION_UP o ACTION_POINTER_UP).
     * * @param pointerId ID del dedo que se ha levantado.
     */
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

    /**
     * Trigonometría y álgebra vectorial de control.
     * Calcula la posición restrictiva de la palometa y
     * normaliza los deltas de dirección (dx, dy).
     */
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

    /**
     * Valida si un punto inicial de pantalla colisiona con el área física circular de la base.
     */
    private boolean dentroDelAreaBase(float px, float py) {
        float ox = px - centroX, oy = py - centroY;
        return Math.sqrt(ox * ox + oy * oy) <= radioBase;
    }
}
