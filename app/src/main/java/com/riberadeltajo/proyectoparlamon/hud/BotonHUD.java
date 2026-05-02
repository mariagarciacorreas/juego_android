package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//botón circular HUD
public class BotonHUD {

    private final float cx;
    private final float cy;
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

        paintFondo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFondo.setColor(Color.argb(160, 20, 20, 30));
        paintFondo.setStyle(Paint.Style.FILL);

        paintBorde = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBorde.setColor(Color.argb(200, 255, 235, 59));
        paintBorde.setStyle(Paint.Style.STROKE);
        paintBorde.setStrokeWidth(2.5f);

        paintJoystic = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintJoystic.setColor(Color.WHITE);
        paintJoystic.setTextSize(radio * 0.9f);
        paintJoystic.setTextAlign(Paint.Align.CENTER);
    }

    public void dibujar(Canvas canvas){
        canvas.drawCircle(cx, cy, radio, paintFondo);
        canvas.drawCircle(cx, cy, radio, paintBorde);
        float offset = -(paintJoystic.ascent() + paintJoystic.descent()) / 2f;
        canvas.drawText(joystic, cx, cy + offset, paintJoystic);
    }

    public boolean dentro(float x, float y){
        float dx = x - cx, dy = y - cy;
        return dx * dx + dy * dy <= radio * radio;
    }
}
