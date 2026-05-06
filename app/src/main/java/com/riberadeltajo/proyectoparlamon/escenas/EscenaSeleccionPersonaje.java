package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

/**
 * Escena de Selección de Personaje.
 * Permite al jugador elegir entre tres campeones (Guerrero, Mago, Elfo)
 * representados visualmente mediante "pastillas" interactivas.
 */
public class EscenaSeleccionPersonaje implements Escena{

    //dependencias
    private Context context;
    private GestorEscenas gestorEscenas;
    private Paint paintTexto;
    private Paint paintPista;
    private Paint paintPastilla;

    //áreas de interacción
    private RectF pastillaPedro;
    private RectF pastillaFeijoo;
    private RectF pastillaAbascal;

    //animación y efectos visuales
    private float escalaPista = 1f;
    private float tiempoPulsacion = 0f;

    //recursos gráficos
    private Bitmap imgGuerrero;
    private Bitmap imgMago;
    private Bitmap imgElfo;



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

        //pista visual (texto que late)
        paintPista = new Paint();
        paintPista.setColor(Color.rgb(255, 235, 59)); // FFEB3B
        paintPista.setTextSize(35);
        paintPista.setTypeface(Typeface.MONOSPACE);
        paintPista.setAntiAlias(false);

        //pastilla
        paintPastilla = new Paint();
        paintPastilla.setColor(Color.DKGRAY);
        paintPastilla.setStyle(Paint.Style.FILL);

        //cargar imagenes de los personajes
        imgGuerrero = BitmapFactory.decodeResource(context.getResources(), R.drawable.seleccion_personaje_guerrero);
        imgMago = BitmapFactory.decodeResource(context.getResources(), R.drawable.seleccion_personaje_mago);
        imgElfo = BitmapFactory.decodeResource(context.getResources(), R.drawable.seleccion_personaje_elfo);

    }

    /**
     * Actualiza la lógica de la animación de "latido".
     */
    @Override
    public void actualizar() {
        // Incrementa el tiempo para oscilar el tamaño del texto de ayuda
        tiempoPulsacion += 0.05f; //velocidad
        // Calcula la escala usando la función seno para un movimiento suave (oscila entre ~0.92 y ~1.08)
        escalaPista = 1f + 0.08f * (float)Math.sin(tiempoPulsacion);

    }

    /**
     * Dibuja la interfaz de selección, calculando posiciones relativas al tamaño del Canvas.
     */
    @Override
    public void renderizar(Canvas canvas) {

        if (canvas == null) return;

        canvas.drawColor(Color.DKGRAY);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        //maquetación proporcional
        float margenLateral = 150;
        float separacion = 80;

        //dividir el ancho disponible entre 3 para crear columnas iguales
        float anchoPastilla = (w - margenLateral*2 - separacion*2) / 3f;
        float altoPastilla = h * 0.8f;

        float espacioArriba = h/2f - altoPastilla/2f;
        float espacioAbajo = h/2f + altoPastilla/2f;

        //pastillas de personajes
        pastillaPedro = new RectF(margenLateral, espacioArriba, margenLateral + anchoPastilla, espacioAbajo);
        pastillaFeijoo = new RectF(pastillaPedro.right + separacion, espacioArriba, pastillaPedro.right + separacion + anchoPastilla, espacioAbajo);
        pastillaAbascal = new RectF(pastillaFeijoo.right + separacion, espacioArriba, pastillaFeijoo.right + separacion + anchoPastilla, espacioAbajo);

        //DIBUJAR PASTILLAS
        canvas.drawRoundRect(pastillaPedro, 30, 30, paintPastilla);
        canvas.drawRoundRect(pastillaFeijoo, 30, 30, paintPastilla);
        canvas.drawRoundRect(pastillaAbascal, 30, 30, paintPastilla);


        //ancho máximo permitido (70% de la pastilla) - autoescalado
        float anchoImg = anchoPastilla * 0.70f;

        //GUERRERO
        if (imgGuerrero != null) {
            float ratio = (float) imgGuerrero.getHeight() / imgGuerrero.getWidth();
            float altoImg = anchoImg * ratio;

            Bitmap scaled = Bitmap.createScaledBitmap(imgGuerrero, (int)anchoImg, (int)altoImg, false);

            float x = pastillaPedro.centerX() - scaled.getWidth() / 2f;
            float y = pastillaPedro.centerY() - scaled.getHeight() / 2f;

            canvas.drawBitmap(scaled, x, y, null);
        }

        //MAGO
        if (imgMago != null) {
            float ratio = (float) imgMago.getHeight() / imgMago.getWidth();
            float altoImg = anchoImg * ratio;

            Bitmap scaled = Bitmap.createScaledBitmap(imgMago, (int)anchoImg, (int)altoImg, false);

            float x = pastillaFeijoo.centerX() - scaled.getWidth() / 2f;
            float y = pastillaFeijoo.centerY() - scaled.getHeight() / 2f;

            canvas.drawBitmap(scaled, x, y, null);
        }

        //ELFO
        if (imgElfo != null) {
            float ratio = (float) imgElfo.getHeight() / imgElfo.getWidth();
            float altoImg = anchoImg * ratio;

            Bitmap scaled = Bitmap.createScaledBitmap(imgElfo, (int)anchoImg, (int)altoImg, false);

            float x = pastillaAbascal.centerX() - scaled.getWidth() / 2f;
            float y = pastillaAbascal.centerY() - scaled.getHeight() / 2f;

            canvas.drawBitmap(scaled, x, y, null);
        }

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

    /**
     * Maneja la selección del personaje detectando en qué área ha pulsado el usuario.
     */
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
