package com.riberadeltajo.proyectoparlamon.mapa;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//control de movimiento libre y velocidad proporcional
public class Joystick {
    private final float centroX;
    private final float centroY;
    private final float radioBase;
    private final float radioPalometa;

    private float palometaX;
    private float palometaY;

    private float dx = 0f;
    private float dy = 0f;
    private boolean activo = false;
    private int pointerIdActivo = -1;

    private final Paint paintBase;
    private final Paint paintBorde;
    private final Paint paintPalometa;

    public Joystick(float centroX, float centroY, float radioBase) {
        this.centroX = centroX;
        this.centroY = centroY;
        this.radioBase = radioBase;
        this.radioPalometa = radioBase * 0.42f;
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
        activo = false; pointerIdActivo = -1;
        palometaX = centroX; palometaY = centroY;
        dx = 0f; dy = 0f;
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
        float ox = px - centroX;
        float oy = py - centroY;
        float dist = (float) Math.sqrt(ox * ox + oy * oy);
        if (dist == 0f) {
            dx = 0f; dy = 0f;
            return;
        }
        float dc = Math.min(dist, radioBase);
        float nx = ox / dist, ny = oy / dist;
        palometaX = centroX + nx * dc;
        palometaY = centroY + ny * dc;
        float mag = dc / radioBase;
        dx = nx * mag;
        dy = ny * mag;

    }

    private boolean dentroDelAreaBase(float px, float py) {
        float ox = px - centroX, oy = py - centroY;
        return Math.sqrt(ox * ox + oy * oy) <= radioBase;
    }
}
