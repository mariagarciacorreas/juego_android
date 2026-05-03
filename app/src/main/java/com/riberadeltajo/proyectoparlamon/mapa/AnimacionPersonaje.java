package com.riberadeltajo.proyectoparlamon.mapa;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/*
Gestión de la animación del sprite del personaje.
Sprite con 5 filas y 5 columnas
fila 0 - ↓ - sur (mirando hacia abajo)
fila 1 - ↙ - sur-oeste (mirando hacia abajo a la izquierda)
fila 2 - ← - oeste (mirando hacia la izquierda)
fila 3 - ↖ - noroeste (mirando hacia arriba a la izquierda)
fila 4 - ↑ - norte (mirando hacia arriba)
            //lo suyo sería tener más sprites para que no cambie el arma de mano
            fila 5 - ↗ - nor-este (mirando hacia arriba a la derecha)
            fila 6 - → - este (mirando hacia la derecha)
            fila 7 - ↘ - sur-este (mirando hacia abajo a la derecha)
 */
public class AnimacionPersonaje {

    private static final int COLUMNAS = 5;
    private static final int FILAS = 5;

    //velocidad de animación - frames que pasan entre cada cambio de imagen
    private static final int FRAMES_POR_PASO = 6;

    private final Bitmap spriteSheet;
    private final int anchoFrame;
    private final int altoFrame;

    private int filaActual = 0; // dirección actual
    private int columnaActual = 0; // frame de animación actual
    private int contadorFrames = 0;

    //tamaño de dibujo en pantalla
    private final int anchoPJ;
    private final int altoPJ;

    //control de volteo de sprite
    private boolean voltear = false;

    public AnimacionPersonaje(Bitmap spriteSheet, int anchoPJ) {
        this.spriteSheet = spriteSheet;
        this.anchoFrame = spriteSheet.getWidth() / COLUMNAS;
        this.altoFrame = spriteSheet.getHeight() / FILAS;
        this.anchoPJ = anchoPJ;
        this.altoPJ = (int) (altoFrame * (anchoPJ / (float) anchoFrame));
    }

    //actualizar cada frame
    //dx y dy son vector de movimiento del personaje
    //en dx=0 y dy=0 el pj no se mueve y se conjela en el frame
    public void actualizar(float dx, float dy) {
        boolean moviendose = Math.abs(dx) > 0.05f || Math.abs(dy) > 0.05f;
        if (moviendose) {
            //determinar fila según dirección dominante
            filaActual = calcularFila(dx, dy);

            //avanzar frame de animación
            contadorFrames++;
            if (contadorFrames >= FRAMES_POR_PASO) {
                contadorFrames = 0;
                columnaActual = (columnaActual + 1) % COLUMNAS;
            }
        } else {
            //parado -> primer frame de la dirección actual
            columnaActual = 0;
            contadorFrames = 0;
        }
    }

    public void dibujar(Canvas canvas, Paint paint, float pantallaX, float pantallaY) {
        Rect origen = new Rect(
                columnaActual * anchoFrame,
                filaActual * altoFrame,
                columnaActual * anchoFrame + anchoFrame,
                filaActual * altoFrame + altoFrame
        );

        Rect destino = new Rect(
                (int) pantallaX,
                (int) pantallaY,
                (int) pantallaX + anchoPJ,
                (int) pantallaY + altoPJ
        );

        if (voltear) {
            //voltear horizontalmente usando matrix
            canvas.save();
            //wspejo: escalar -1 en X alrededor del centro del sprite
            float cx = pantallaX + anchoPJ / 2f;
            float cy = pantallaY + altoPJ  / 2f;
            canvas.scale(-1f, 1f, cx, cy);
            canvas.drawBitmap(spriteSheet, origen, destino, paint);
            canvas.restore();
        } else {
            canvas.drawBitmap(spriteSheet, origen, destino, paint);
        }
    }

    //calcular la fila del sprite según el vector de movimiento
    private int calcularFila(float dx, float dy){
        // atan2 devuelve ángulo en radianes: 0=derecha, PI/2=abajo, -PI/2=arriba
        double angulo = Math.toDegrees(Math.atan2(dy, dx));

        //ajustar ángulo correcto
        /*
            Dirección real	dx/dy	atan2 original	angulo corregido
            -------------   -----   --------------  ---------------
            Derecha →   	1,0  	0°	            90°
            Arriba ↑	    0,-1	270°	        0°
            Izquierda ←  	-1,0	180°	        270°
            Abajo ↓     	0,1	    90°	            180°
         */
        angulo = (angulo + 450) % 360; // +90° y normalizar


        if(angulo < 0) {
            angulo += 360;
        }

        //derecha = voltear la imagen
        voltear = dx > 0;


        // ↓ - sur (mirando hacia abajo)
        if (angulo >= 157.5 && angulo < 217.5) return 0;

        // ← - oeste (mirando hacia la izquierda)
        if (angulo >= 217.5 && angulo < 262.5) return 2;

        // ↙ - sur-oeste (mirando hacia abajo a la izquierda)
        if (angulo >= 262.5 && angulo < 292.5) return 1;

        // ↖ - noroeste (mirando hacia arriba a la izquierda)
        if (angulo >= 292.5 && angulo < 330) return 3;

        // ↑ - norte (mirando hacia arriba)
        if (angulo >= 330 || angulo < 22.5) return 4;

        // ↗ - nor-este (mirando hacia arriba a la derecha) //voltear imagen
        if (angulo >= 22.5 && angulo < 67.5) return 3;

        // → - este (mirando hacia la derecha) //voltear imagen
        if (angulo >= 67.5 && angulo < 112.5) return 2;

        // ↘ - sur-este (mirando hacia abajo a la derecha) //voltear imagen
        if (angulo >= 112.5 && angulo < 157.5) return 1;


        Log.d("ANGULO", "dx=" + dx + " dy=" + dy + " ang=" + angulo);


        return 0;
    }

    public int getAnchoPJ() {
        return anchoPJ;
    }

    public int getAltoPJ() {
        return altoPJ;
    }
}
