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

        // Escalar igual que el mapa visual
        int anchoEscalado = Math.round(original.getWidth()  * zoomFactor);
        int altoEscalado  = Math.round(original.getHeight() * zoomFactor);
        mapa = Bitmap.createScaledBitmap(original, anchoEscalado, altoEscalado, false);
        original.recycle();
    }

    /**
     * Devuelve true si el píxel es negro
     * Esto indica que es transitable la posición mundoX, mundoY
     */
    public boolean esTransitable(float mundoX, float mundoY) {
        int px = (int) mundoX;
        int py = (int) mundoY;

        // Fuera del mapa = no transitable
        if (px < 0 || py < 0 || px >= mapa.getWidth() || py >= mapa.getHeight()) {
            return false;
        }

        int pixel = mapa.getPixel(px, py);
        int brillo = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;

        // Negro  = transitable
        return brillo < 128;
    }

    /**
     * Comprueba si un rectángulo del personaje es completamente transitable
     * Comprueba las 4 esquinas del sprite
     */
    public boolean rectTransitable(float mundoX, float mundoY, int ancho, int alto) {
        // Reducimos un poco el hitbox para que no sea tan estricto (margen de 4px)
        int margen = 4;
        return esTransitable(mundoX + margen,mundoY + margen)
                && esTransitable(mundoX + ancho - margen, mundoY + margen)
                && esTransitable(mundoX + margen, mundoY + alto - margen)
                && esTransitable(mundoX + ancho - margen, mundoY + alto - margen);
    }

    public void recycle() {
        if (mapa != null) {
            mapa.recycle();
            mapa = null;
        }
    }
}
