package com.riberadeltajo.proyectoparlamon.mapa;

/**
 * El enumerado TipoControl define de forma estricta los esquemas de entrada (input)
 * disponibles en el juego para el desplazamiento del protagonista por el mapa.
 * * Actúa como un interruptor lógico de tipado seguro que el GestorHUD,
 * el OverlayAjustes y el GestorControles utilizan para conmutar la interfaz activa.
 */
public enum TipoControl {
    JOYSTICK,
    FLECHAS
}
