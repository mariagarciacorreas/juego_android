package com.riberadeltajo.proyectoparlamon.motor;

import android.graphics.Canvas;

import com.riberadeltajo.proyectoparlamon.escenas.Escena;

/**
 * Clase para gestionar las escenas a mostrar en el transcurso del juego
 */
public class GestorEscenas {

    private Escena escenaActual;

    public void cambiarEscena(Escena nuevaEscena){
        escenaActual = nuevaEscena;
    }

    public void actualizar(){
        if(escenaActual != null){
            escenaActual.actualizar();
        }
    }

    public void renderizar(Canvas canvas){
        if(escenaActual != null){
            escenaActual.renderizar(canvas);
        }
    }

    public void onTouch(float x, float y){
        if(escenaActual != null){
            escenaActual.onTouch(x, y);
        }
    }

}
