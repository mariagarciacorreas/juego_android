package com.riberadeltajo.proyectoparlamon.mapa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.riberadeltajo.proyectoparlamon.R;


public class MapaColisiones {

    private Bitmap mapa;


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


    public void recycle() {
        if (mapa != null) {
            mapa.recycle();
            mapa = null;
        }
    }
}
