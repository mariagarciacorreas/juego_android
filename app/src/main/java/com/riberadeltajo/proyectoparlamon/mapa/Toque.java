package com.riberadeltajo.proyectoparlamon.mapa;

/**
 * La clase Toque actúa como un contenedor de datos para eventos táctiles.
 * Su objetivo es abstraer las coordenadas y el identificador de un dedo sobre la pantalla,
 * facilitando la gestión de entradas multitáctiles en el Gestor de Controles.
 */
public class Toque {
    public int x, y, index;

    public Toque(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }
}