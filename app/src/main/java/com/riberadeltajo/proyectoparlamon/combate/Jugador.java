package com.riberadeltajo.proyectoparlamon.combate;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa al jugador durante el combate.
 * Recibe el nombre y clase elegidos en la pantalla de selección.
 */
public class Jugador {

    private final String nombre;
    private final String clase;
    private int vida;
    private final int vidaMax;

    private final int velocidad;
    private final List<Ataques> ataques;

    public Jugador(String nombre, String clase) {
        this.nombre = nombre;
        this.clase  = clase;
        this.vidaMax = calcularVidaPJ();
        this.vida = vidaMax;
        this.velocidad = calcularVelocidad();
        this.ataques = new ArrayList<>();
        inicializarAtaques();
    }

    private void inicializarAtaques() {
        switch (clase.toLowerCase()) {
            case "guerrero":
                ataques.add(new Ataques("Espada constitucional", 120, "Un golpe con la carta magna", 70, 5));
                ataques.add(new Ataques("Escudo parlamentario",   100, "Reduce el daño enemigo y contraataca", 90, 10 ));
                ataques.add(new Ataques("Carga democrática",     50, "Embestida con todo el poder del voto", 100, 20 ));
                ataques.add(new Ataques("Moción de censura",     150, "Elimina el turno del enemigo (daño extra)", 50, 15 ));
                break;
            case "mago":
                ataques.add(new Ataques("Hechizo del BOE",       200, "Invoca legislación vinculante", 40, 10 ));
                ataques.add(new Ataques("Decreto mágico",        40, "Emite un decreto que confunde al enemigo", 100, 20 ));
                ataques.add(new Ataques("Bola de fuego fiscal",  50, "Un impuesto devastador en forma de bola de fuego", 100, 20 ));
                ataques.add(new Ataques("Teletransporte al Senado", 70, "Desaparece y reaparece dando un golpe", 85, 15 ));
                break;
            case "elfo":
            default:
                ataques.add(new Ataques("Flecha del IRPF",60, "Una flecha precisa llena de impuestos", 90, 15));
                ataques.add(new Ataques("Tiro con el acta",180, "Lanza el acta electoral al enemigo", 70, 5 ));
                ataques.add(new Ataques("Sigilo élfico", 40, "Ataque furtivo desde las sombras", 90, 20 ));
                ataques.add(new Ataques("Lluvia de proposiciones", 70, "Múltiples proposiciones no de ley", 100, 15 ));
                break;
        }
    }

    private int calcularVidaPJ(){
        switch (clase.toLowerCase()){
            case "guerrero":
                return 3114;
            case "mago":
                return 1599;
            case "elfo":
                return 2235;
            default:
                return 2000;
        }
    }
    private int calcularVelocidad(){
        switch (clase.toLowerCase()){
            case "guerrero":
                return 40;
            case "mago":
                return 60;
            case "elfo":
                return 90;
            default:
                return 50;
        }
    }
    public void recibirDanio(int cantidad) {
        vida = Math.max(0, vida - cantidad);
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    /** Devuelve el porcentaje de HP restante (0.0 - 1.0). */
    public float getPorcentajeHp() {
        return (float) vida / vidaMax;
    }

    public String getNombre(){
        return nombre;
    }
    public String getClase(){
        return clase;
    }
    public int getVida(){
        return vida;
    }
    public int getVidaMax(){
        return vidaMax;
    }
    public int getVelocidad(){
        return velocidad;
    }
    public List<Ataques> getAtaques(){
        return ataques;
    }
}
