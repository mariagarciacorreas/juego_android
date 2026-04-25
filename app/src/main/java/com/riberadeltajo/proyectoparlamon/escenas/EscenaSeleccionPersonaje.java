package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

public class EscenaSeleccionPersonaje implements Escena{

    private Context context;
    private GestorEscenas gestorEscenas;
    private Paint paintTexto;
    private Paint paintPista;
    private Paint paintPastilla;

    private RectF pastillaPedro;
    private RectF pastillaFeijoo;
    private RectF pastillaAbascal;

    private float escalaPista = 1f;
    private float tiempoPulsacion = 0f;


    public EscenaSeleccionPersonaje(Context context, GestorEscenas gestorEscenas) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;

        //texto
        paintTexto = new Paint();
        paintTexto.setColor(Color.DKGRAY);
        paintTexto.setTextSize(50);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paintTexto.setAntiAlias(false);

        //pista
        paintPista = new Paint();
        paintPista.setColor(Color.GRAY);
        paintPista.setTextSize(35);
        paintPista.setTypeface(Typeface.MONOSPACE);
        paintPista.setAntiAlias(false);

        //pastilla
        paintPastilla = new Paint();
        paintPastilla.setColor(Color.rgb(255, 235, 59)); // FFEB3B
        paintPastilla.setStyle(Paint.Style.FILL);

    }

    @Override
    public void actualizar() {

        tiempoPulsacion += 0.05f; //velocidad
        escalaPista = 1f + 0.08f * (float)Math.sin(tiempoPulsacion);

    }

    @Override
    public void renderizar(Canvas canvas) {

        canvas.drawColor(Color.DKGRAY);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        float margenLateral = 120;
        float separacion = 40;

        float anchoPastilla = (w - margenLateral*2 - separacion*2) / 3f;
        float altoPastilla = h * 0.75f;

        float espacioArriba = h/2f - altoPastilla/2f;
        float espacioAbajo = h/2f + altoPastilla/2f;

        //pastillas de personajes
        pastillaPedro = new RectF(margenLateral, espacioArriba, margenLateral + anchoPastilla, espacioAbajo);
        pastillaFeijoo = new RectF(pastillaPedro.right + separacion, espacioArriba, pastillaPedro.right + separacion + anchoPastilla, espacioAbajo);
        pastillaAbascal = new RectF(pastillaFeijoo.right + separacion, espacioArriba, pastillaFeijoo.right + separacion + anchoPastilla, espacioAbajo);

        //dibujar pastillas
        canvas.drawRoundRect(pastillaPedro, 30, 30, paintPastilla);
        canvas.drawRoundRect(pastillaFeijoo, 30, 30, paintPastilla);
        canvas.drawRoundRect(pastillaAbascal, 30, 30, paintPastilla);

        //texto
        canvas.drawText("Pedro Sánchez", pastillaPedro.left + 20, pastillaPedro.top + 80, paintTexto);
        canvas.drawText("Feijóo", pastillaFeijoo.left + 20, pastillaFeijoo.top + 80, paintTexto);
        canvas.drawText("Abascal", pastillaAbascal.left + 20, pastillaAbascal.top + 80, paintTexto);

        //margen inferior
        float margen = 60;

        //posición Y de la pista (abajo derecha)
        float pistaY = canvas.getHeight() - margen;

        //indicador
        //dibujar latido

        String textoPista = "▲ Elige tu personaje";
        //guardar ancho y alto de texto pista
        float anchoPista = paintPista.measureText(textoPista);
        float altoPista = paintPista.getTextSize();
        //posición: esquina inferior derecha
        float eje_x = canvas.getWidth() - anchoPista - margen;
        float eje_y = pistaY;

        //escala de que simula latido
        canvas.save();
        canvas.translate(eje_x + anchoPista / 2f, eje_y); //establecer origen en el centro del texto
        canvas.scale(escalaPista, escalaPista);
        canvas.translate( -(eje_x + anchoPista / 2f), -eje_y);

        canvas.drawText(textoPista, eje_x, eje_y, paintPista);

        canvas.restore();
    }

    @Override
    public void onTouch(float x, float y) {

        if (pastillaPedro.contains(x, y)) {
            gestorEscenas.cambiarEscena(new EscenaSeleccionPersonajeDetalle(context, gestorEscenas, "Pedro Sánchez", "Guerrero"));
        }

        if (pastillaFeijoo.contains(x, y)) {
            gestorEscenas.cambiarEscena(new EscenaSeleccionPersonajeDetalle(context, gestorEscenas, "Feijóo", "Mago"));
        }

        if (pastillaAbascal.contains(x, y)) {
            gestorEscenas.cambiarEscena(new EscenaSeleccionPersonajeDetalle(context, gestorEscenas, "Abascal", "Elfo"));
        }
    }
}
