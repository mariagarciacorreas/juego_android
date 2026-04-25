package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

public class EscenaSeleccionPersonajeDetalle implements Escena{

    private Context context;
    private GestorEscenas gestorEscenas;

    private String nombre;
    private String clase;

    private Paint paintTexto;
    private Paint paintBoton;
    private Paint paintTextoBtn;

    //tamaño de pantalla
    float w;
    float h;

    public EscenaSeleccionPersonajeDetalle(Context context, GestorEscenas gestorEscenas, String nombre, String clase) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;
        this.nombre = nombre;
        this.clase = clase;

        paintTexto = new Paint();
        paintTexto.setColor(Color.rgb(255, 235, 59)); // FFEB3B
        paintTexto.setTextSize(50);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paintTexto.setAntiAlias(false);

        paintTextoBtn = new Paint();
        paintTextoBtn.setColor(Color.DKGRAY);
        paintTextoBtn.setTextSize(50);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paintTexto.setAntiAlias(false);

        paintBoton = new Paint();
        paintBoton.setColor(Color.WHITE);
        paintBoton.setStyle(Paint.Style.FILL);
    }

    @Override
    public void actualizar() {

    }

    @Override
    public void renderizar(Canvas canvas) {

        canvas.drawColor(Color.DKGRAY);

        w = canvas.getWidth();
        h = canvas.getHeight();

        //nombre y clase
        canvas.drawText(nombre, w/2f - 150, 150, paintTexto);
        canvas.drawText("Clase: " + clase, w/2f - 150, 230, paintTexto);

        //sprite del personaje
        canvas.drawText("[SPRITE AQUÍ]", w/2f - 150, h/2f, paintTexto);

        //botón comenzar
        float bw = 400;
        float bh = 120;
        float bx = w/2f - bw/2f;
        float by = h - 200;

        canvas.drawRect(bx, by, bx + bw, by + bh, paintBoton);
        canvas.drawText("Comenzar", bx + 80, by + 75, paintTextoBtn);

    }

    @Override
    public void onTouch(float x, float y) {

        float bw = 400;
        float bh = 120;
        float bx = w/2f - bw/2f;
        float by = h - 200;

        if (bx >= bx && x <= bx + bw && y >= by && y <= by + bh) {
            //TODO
            //gestorEscenas.cambiarEscena(new EscenaMapa(context, gestorEscenas));
        }
    }
}
