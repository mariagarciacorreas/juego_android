package com.riberadeltajo.proyectoparlamon.motor;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.riberadeltajo.proyectoparlamon.escenas.EscenaInicio;

public class Juego extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private BucleJuego bucle;

    private GestorEscenas gestorEscenas;


    private static final String TAG = Juego.class.getSimpleName();

    public Juego(Activity context) {
        super(context);
        //iniciar surfaceHolder
        holder = getHolder();
        holder.addCallback(this);

        //crear gestor de escenas
        gestorEscenas = new GestorEscenas();
        //establecer la primera escena del juego
        gestorEscenas.cambiarEscena(new EscenaInicio(context, gestorEscenas));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);

        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //comenzar el bucle
        bucle.start();

    }

    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */
    public void actualizar() {

        gestorEscenas.actualizar();

    }

    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas) {


        //canvas.drawColor(Color.BLACK);

        gestorEscenas.renderizar(canvas);

        //DEBUG
        //pintar mensajes que nos ayudan
//        Paint p=new Paint();
//        p.setStyle(Paint.Style.FILL_AND_STROKE);
//        p.setColor(Color.RED);
//        p.setTextSize(50);
//        canvas.drawText("Frame "+bucle.iteraciones+";"+"Tiempo "+bucle.tiempoTotal + " ["+bucle.maxX+","+bucle.maxY+"]",50,150,p);



    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        //gesión multitouch para EscenaMapa
        gestorEscenas.onTouchEvent(event);

        //gestión de toque simple para escenas
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            gestorEscenas.onTouch(event.getX(), event.getY());
        }
        return true;
    }

}
