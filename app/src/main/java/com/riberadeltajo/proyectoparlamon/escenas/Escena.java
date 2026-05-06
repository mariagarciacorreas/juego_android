package com.riberadeltajo.proyectoparlamon.escenas;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Interfaz que define el ciclo de vida básico y el control de eventos
 * para cualquier escena o pantalla dentro del juego Parlamon.
 * Todas las pantallas (Menú, Mapa, Combate, Diálogos, etc.) deben implementar esta interfaz.
 */
public interface Escena {

    /**
     * Actualizar lógica de escena.
     * Este método es invocado de forma cíclica y constante por el bucle principal del juego (Game Loop).
     */
    void actualizar();

    /**
     * Dibujar en pantalla la escena
     * @param canvas
     */
    void renderizar(Canvas canvas);

    /**
     * Manejar pulsaciones simples en la pantalla táctil.
     * Se pasan por parámetro las coordenadas de la pulsación en pantalla.
     * @param x
     * @param y
     */
    void onTouch(float x, float y);

    /**
     * Gestiona eventos táctiles avanzados o complejos (multitouch, gestos de arrastre, etc.).
     * * @param event El objeto MotionEvent de Android que contiene toda la información del evento táctil.
     */
    default void onTouchEvent(MotionEvent event){};
}
