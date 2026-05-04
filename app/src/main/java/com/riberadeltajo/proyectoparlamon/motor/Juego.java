package com.riberadeltajo.proyectoparlamon.motor;

import android.app.Activity;
import android.graphics.Canvas;
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
        holder = getHolder();
        holder.addCallback(this);
        gestorEscenas = new GestorEscenas();
        gestorEscenas.cambiarEscena(new EscenaInicio(context, gestorEscenas));
    }

    /**
     * Fuerza que la SurfaceView ocupe exactamente todo el espacio disponible.
     * Sin esto, en algunos dispositivos con notch o barra de gestos la surface
     * se queda corta y aparecen bandas negras en los bordes.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width  = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // Si alguna dimensión llega a 0 antes del primer layout, comportamiento por defecto
        if (width == 0 || height == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Las escenas obtienen las dimensiones del canvas en su inicializacionDiferida()
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        getHolder().addCallback(this);
        bucle = new BucleJuego(getHolder(), this);
        setFocusable(true);
        bucle.start();
    }

    public void actualizar() {
        gestorEscenas.actualizar();
    }

    public void renderizar(Canvas canvas) {
        gestorEscenas.renderizar(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        boolean retry = true;
        while (retry) {
            try {
                bucle.join();
                retry = false;
            } catch (InterruptedException e) { /* reintentamos */ }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestorEscenas.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            gestorEscenas.onTouch(event.getX(), event.getY());
        }
        return true;
    }
}