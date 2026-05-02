package com.riberadeltajo.proyectoparlamon.mapa;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.riberadeltajo.proyectoparlamon.MainActivity;

//clase surface (lienzo en donde "dibujamos" para el juego
//callback para saber cuando se crea en nuestra superficie
//runnable para ejecutar el loop jugable del juego en un hilo
public class Juego extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread hilo;
    private boolean ejecutando = true;

    private float x, y;              //coordenadas del personaje
    private float velocidad = 12;    //velocidad de movimiento

    //variables públicas para que MainActivity pueda modificarlas
    public boolean arriba, abajo, izquierda, derecha;

    //evita dibujar antes de que la pantalla tenga tamaño real
    private boolean pantallaLista = false;

    public Juego(MainActivity context) {
        super(context);
        getHolder().addCallback(this);

        x = 300;
        y = 300;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ejecutando = true;
        hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        while (ejecutando) {

            //si la superficie aún no está lista, saltamos
            if (!getHolder().getSurface().isValid()) continue;

            //esperamos al primer frame donde getWidth() y getHeight() ya tienen valor
            if (!pantallaLista && getWidth() > 0 && getHeight() > 0) {
                pantallaLista = true;
            }

            actualizar();

            Canvas c = getHolder().lockCanvas();
            renderizar(c);
            getHolder().unlockCanvasAndPost(c);

            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }

    //actualiza la posicion del personaje segun las teclas pulsadas
    private void actualizar() {
        if (!pantallaLista) return; //evita cálculos antes de tiempo

        if (arriba)    y -= velocidad;
        if (abajo)     y += velocidad;
        if (izquierda) x -= velocidad;
        if (derecha)   x += velocidad;
    }

    //dibuja todo en pantalla
    private void renderizar(Canvas canvas) {
        if (!pantallaLista) return; //evita pantalla gris

        canvas.drawColor(Color.GRAY);

        Paint p = new Paint();
        p.setColor(Color.WHITE);

        //personaje
        canvas.drawCircle(x, y, 40, p);
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override public void surfaceDestroyed(SurfaceHolder holder) { ejecutando = false; }
}