package com.riberadeltajo.proyectoparlamon.escenas;

import android.graphics.Canvas;
import android.view.MotionEvent;


public interface Escena {


    void actualizar();


    void renderizar(Canvas canvas);


    void onTouch(float x, float y);


    default void onTouchEvent(MotionEvent event){};
}
