package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.dialogos.EscritorTexto;
import com.riberadeltajo.proyectoparlamon.dialogos.GestorDialogos;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

import java.util.Arrays;
import java.util.List;

public class EscenaInicio implements Escena{

    private Context context;
    private GestorEscenas gestorEscenas;
    private EscritorTexto escritor;

    //tres fragmentos de texto (o, 1, 2)
    private int pantallaDialogoActual = 0;

    private float escalaPista = 1f;
    private float tiempoPulsacion = 0f;

    //imagen Ciber Frank
    private Bitmap imgCiberFrank;
    private int alphaCiberFrank = 0;
    private boolean mostrarCiberFrank = false;
    private boolean ocultandoCiberFrank = false;

    private GestorDialogos gestorDialogos = new GestorDialogos();
    private final List<List<String>> dialogosLore = gestorDialogos.getDialogosLore();


    private Paint paintTexto;
    private Paint paintPista;

    public EscenaInicio(Context context, GestorEscenas gestorEscenas) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;

        //pintar texto
        paintTexto = new Paint();
        paintTexto.setColor(Color.WHITE);
        paintTexto.setTextSize(35);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setAntiAlias(false);

        //pintar pista
        paintPista = new Paint();
        paintPista.setColor(Color.GRAY);
        paintPista.setTextSize(35);
        paintPista.setTypeface(Typeface.MONOSPACE);
        paintPista.setAntiAlias(false);

        //cargar imagen Ciber Frank y escalar
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.ciberfrank_intro);
        int anchoCiberFrank = 600; // px — ajusta a tu gusto
        int altoCiberFrank = (int)(original.getHeight() * (anchoCiberFrank / (float) original.getWidth()));
        imgCiberFrank = Bitmap.createScaledBitmap(original, anchoCiberFrank, altoCiberFrank, true);
        original.recycle();

        //50 milisegundos enre letra y letra
        escritor = new EscritorTexto(50);
        cargarPantalla(0);
    }

    private void cargarPantalla(int index){
        pantallaDialogoActual = index;
        escritor.iniciarEscritura(dialogosLore.get(index));

        //cargar Ciber Frank
        if (index == 0){
            mostrarCiberFrank = false;
            ocultandoCiberFrank = false;
            alphaCiberFrank = 0; //fade-in
        }else{
            ocultandoCiberFrank = true; //fade-out
        }
    }

    @Override
    public void actualizar() {
        escritor.actualizarEscritura();

        //animnación texto pista
        if(escritor.isTerminado()){
            tiempoPulsacion += 0.05f; //velocidad
            escalaPista = 1f + 0.08f * (float)Math.sin(tiempoPulsacion);
        }

        //trigger de fade-in - al llegar a la línea 4 del texto
        if(pantallaDialogoActual == 0 && !mostrarCiberFrank && escritor.getLineaActual() >= 4){
            mostrarCiberFrank = true;
            ocultandoCiberFrank = false;
            alphaCiberFrank = 0;
        }

        //fade-in Ciber Frank
        if(mostrarCiberFrank && !ocultandoCiberFrank){
            if(alphaCiberFrank < 255){
                alphaCiberFrank = Math.min(255, alphaCiberFrank + 5);
            }
        }

        //fade-out Ciber Frank
        if(ocultandoCiberFrank){
            if(alphaCiberFrank > 0){
                alphaCiberFrank = Math.max(0, alphaCiberFrank - 5);
            }
        }
    }

    @Override
    public void renderizar(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);

        String[] lineas = escritor.getLineasVisibles();

        //calcular altura de cada línea
        float lineHeight = paintTexto.getTextSize() + 20;

        //texto pista
        String textoPista = "▼ Pulsa para continuar";
        //guardar ancho y alto de texto pista
        float anchoPista = paintPista.measureText(textoPista);
        float altoPista = paintPista.getTextSize();

        //guardar altura completa del bloque de texto
        float alturaBloqueTexto = lineas.length * lineHeight;

        //margen inferior
        float margen = 60;

        //posición Y de la pista (abajo derecha)
        float pistaY = canvas.getHeight() - margen;

        //posición Y inicial del texto (encima de la pista)
        float textoY = pistaY - altoPista - 40 - alturaBloqueTexto; //separación 40px

        //dibujar imagen Ciber Frank
        if(alphaCiberFrank > 0){
            Paint p = new Paint();
            p.setAlpha(alphaCiberFrank);

            float x = canvas.getWidth() - canvas.getWidth() / 2f + imgCiberFrank.getWidth() / 2f;
            float y = canvas.getHeight() / 2f - imgCiberFrank.getHeight() / 2f ;

            canvas.drawBitmap(imgCiberFrank, x, y, p);
        }

        //dibujer texto del diálogo encima de la pista
        float y = textoY;
        for(String li : lineas){
            canvas.drawText(li, 130, y, paintTexto);
            y += lineHeight;
        }

        //Pista para seguir con el flujo del juego
        //dibujar latido
        if(escritor.isTerminado()){

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
    }


    @Override
    public void onTouch(float x, float y) {

        if(!escritor.isTerminado()){
            //si no ha terminado, completar texto de golpe
            escritor.completarEscritura();

            //si es la pantalla 0, mostrar imagen Ciber Frank
            if(pantallaDialogoActual == 0){
                mostrarCiberFrank = true;
                ocultandoCiberFrank = false;
                alphaCiberFrank = 255; //visible al 100%
            }

        }else{
            //cuando termina el texto, avanzar pantalla (si quedan) o ir a selección de personaje
            int siguiente = pantallaDialogoActual + 1;
            if(siguiente < dialogosLore.size()){
                cargarPantalla(siguiente);
            }else{
                //termina la escritura y pasa a la siguiente escena
                gestorEscenas.cambiarEscena(new EscenaSeleccionPersonaje(context, gestorEscenas));
            }
        }
    }
}
