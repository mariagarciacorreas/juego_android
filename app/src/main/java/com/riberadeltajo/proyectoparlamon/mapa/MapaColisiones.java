package com.riberadeltajo.proyectoparlamon.mapa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.riberadeltajo.proyectoparlamon.R;

/**
 * MapaColisiones gestiona las zonas transitables y los obstáculos del mapa.
 * Utiliza una técnica de máscara de bits (Hitmap) basada en un Bitmap donde los
 * píxeles oscuros representan zonas transitalbes y los píxeles claros representan zonas no transitables.
 */
public class MapaColisiones {

    private Bitmap mapa;

    /**
     * Constructor: Carga la imagen de colisiones y la escala exactamente al mismo tamaño
     * que el mapa visual para sincronizar las coordenadas lógicas con las gráficas.
     * @param context
     * @param zoomFactor
     */
    public MapaColisiones(Context context, float zoomFactor) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.memopolis_colisiones);

        // Calcula las dimensiones escaladas en función del zoom del juego para mantener la correspondencia 1:1
        int anchoEscalado = Math.round(original.getWidth()  * zoomFactor);
        int altoEscalado  = Math.round(original.getHeight() * zoomFactor);

        // Crea el bitmap de colisiones definitivo y adaptado
        mapa = Bitmap.createScaledBitmap(original, anchoEscalado, altoEscalado, false);
        // Libera la memoria nativa de la imagen original para optimizar la RAM
        original.recycle();
    }

    /**
     * Evalúa si un punto discreto (x,y) del mundo transitable analizando el brillo del píxel.
     * @param mundoX
     * @param mundoY
     * @return true si el suelo es transitable (píxel oscuro); false si es un obstáculo o está fuera del mapa.
     */
    public boolean esTransitable(float mundoX, float mundoY) {
        int px = (int) mundoX;
        int py = (int) mundoY;

        // Control perimetral preventivo: Fuera de los límites del mapa se considera obstáculo sólido
        if (px < 0 || py < 0 || px >= mapa.getWidth() || py >= mapa.getHeight()) {
            return false;
        }

        // Extrae el color hexadecimal del pixel en esa coordenada
        int pixel = mapa.getPixel(px, py);
        // Calcula el brillo promedio (escala de grises) del píxel
        int brillo = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;

        return brillo < 128; // negro = transitable
    }

    /**
     * Comprueba si el rectángulo delimitador (Hitbox) de una entidad puede moverse a una posición.
     * Evalúa únicamente las 4 esquinas del rectángulo aplicando un pequeño margen de tolerancia.
     * * @param mundoX Futura coordenada X propuesta para el personaje.
     * @param mundoY Futura coordenada Y propuesta para el personaje.
     * @param ancho Anchura en píxeles del personaje.
     * @param alto Altura en píxeles del personaje.
     * @return true si las cuatro esquinas están sobre terreno transitable.
     */
    public boolean rectTransitable(float mundoX, float mundoY, int ancho, int alto) {
        // Margen interno de seguridad (en píxeles) para evitar que el personaje se quede trabado
        // en esquinas afiladas debido a imprecisiones de redondeo flotante.
        int margen = 4;
        // Para que todo el cuerpo pase, las 4 esquinas deben dar positivo en transitabilidad:
        return esTransitable(mundoX + margen,              mundoY + margen)
                && esTransitable(mundoX + ancho - margen, mundoY + margen)
                && esTransitable(mundoX + margen,         mundoY + alto - margen)
                && esTransitable(mundoX + ancho - margen, mundoY + alto - margen);
    }

    /**
     * Destructor manual de seguridad. Libera el mapa de la memoria de la GPU
     * cuando la escena del mapa se cierra (por ejemplo, al entrar en un combate).
     */
    public void recycle() {
        if (mapa != null) {
            mapa.recycle();
            mapa = null;
        }
    }
}
