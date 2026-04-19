package com.riberadeltajo.proyectoparlamon.escenas;

import android.graphics.Canvas;

public interface Escena {

    //actualizar lógica de escena
    void actualizar();

    //dibujar en pantalla la escena
    void renderizar(Canvas canvas);

    //manejar toques en pantalla
    void onTouch(float x, float y);
}
