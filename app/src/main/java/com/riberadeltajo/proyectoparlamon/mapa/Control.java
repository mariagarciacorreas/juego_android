package com.riberadeltajo.proyectoparlamon.mapa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/*
Botón táctil que detecta ser tocado en pantalla
 */
public class Control {

    public boolean pulsado = false;
    public float x, y;
    private Bitmap imagen;

    //tamaño fijo flechas
    //private final int TAMANIO_PX = 70;

    private final Paint paintFondo;

    public Control(Context c, float x, float y, int recurso, int tamanioPx) {
        this.x = x;
        this.y = y;
        Bitmap original = BitmapFactory.decodeResource(c.getResources(), recurso);
        imagen = Bitmap.createScaledBitmap(original, tamanioPx, tamanioPx, true);
        original.recycle();

        paintFondo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFondo.setColor(android.graphics.Color.argb(160, 255, 220, 0));
        paintFondo.setStyle(android.graphics.Paint.Style.FILL);
    }

    public void dibujar(Canvas c, Paint p) {

        //fondo flecha
        float cx = x + imagen.getHeight() / 2f;
        float cy = y + imagen.getHeight() / 2f;
        c.drawCircle(cx, cy, imagen.getWidth() / 2f + 8, paintFondo);

        //flecha
        p.setAlpha(pulsado ? 255 : 200);
        c.drawBitmap(imagen, x, y, p);
        p.setAlpha(255);
    }

    public boolean dentro(float px, float py) {
        return px > x && px < x + imagen.getWidth() &&
                py > y && py < y + imagen.getHeight();
    }

    public int ancho() { return imagen.getWidth(); }
    public int alto() { return imagen.getHeight(); }
}