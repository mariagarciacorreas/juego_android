package com.riberadeltajo.proyectoparlamon.combate;

import java.util.ArrayList;
import java.util.List;


public class Jefe {

    public enum Fase { FASE_1, FASE_2 }

    private int vidaEnemigo;
    private int vidaEnemigoMax;
    private Fase faseActual;
    private int velocidadJefe;

    private List<Ataques> ataquesFase1;
    private List<Ataques> ataquesFase2;

    /*
    Frases que dice el jefe al atacar (por fase)
    Las frases se eligen al azar con el metodo getFraseAtaque
     */
    private final String[] frasesFase1 = {
            "¡Esto es lo que pasa por votar!",
            "¡La constitución es mía ahora!",
            "¡Silencio, ciudadano rebelde!",
            "¡El orden lo pongo yo!"
    };

    private final String[] frasesFase2 = {
            "¡¡MODO DICTADOR ACTIVADO!!",
            "¡No puedes con el VERDADERO poder!",
            "¡Ignoráis mi grandeza!",
            "¡AQUI MANDO YO!"
    };

    public Jefe() {
        this.vidaEnemigoMax = 1975;
        this.vidaEnemigo = vidaEnemigoMax;
        this.faseActual = Fase.FASE_1;
        this.velocidadJefe = 55;

        listaAtaquesJefeFase1();
        listaAtaquesJefeFase2();


    }

    public List<Ataques> listaAtaquesJefeFase1(){
        ataquesFase1 = new ArrayList<>();
        ataquesFase1.add(new Ataques("Decreto autoritario",   100, "Decreto con efecto inmediato", 80, 10));
        ataquesFase1.add(new Ataques("Hackeo propagandístico", 20, "Llena tu mente de desinformación", 100, 20));
        ataquesFase1.add(new Ataques("Censura digital",       50, "Silencia tus habilidades un turno", 100, 10));
        ataquesFase1.add(new Ataques("Robo de libertades",    50, "Te quita libertades y puntos de vida", 100, 10));

        return ataquesFase1;
    }

    public List<Ataques> listaAtaquesJefeFase2(){
        ataquesFase2 = new ArrayList<>();
        ataquesFase2.add(new Ataques("PURGA MASIVA",          100, "Un ataque brutal del dictador enfurecido", 70, 10));
        ataquesFase2.add(new Ataques("Ley marcial digital",   100, "Declara el estado de excepción", 90, 10));
        ataquesFase2.add(new Ataques("Bunker.exe",            150, "Llama a sus seguidores digitales", 90, 8));
        ataquesFase2.add(new Ataques("FRANQUISMO 2.0",        300, "Su ataque definitivo en fase de furia", 50, 5));

        return ataquesFase2;
    }

    /*
    Metodo que, en función de la fase del enemigo,
    elige un ataque al azar que utilizará en el combate en un determinado turno.
    Utilizamos una lista extra para filtrar los ataques que aún tienen usos (PP).
     */
    public Ataques elegirAtaque() {
        List<Ataques> listaAtaques;
        if (faseActual == Fase.FASE_1) {
            listaAtaques = ataquesFase1;
        } else {
            listaAtaques = ataquesFase2;
        }

        List<Ataques> ataquesDisponibles = new ArrayList<>();
        for (Ataques ataques: listaAtaques){
            if (ataques.usoAtaquePosible()){
                ataquesDisponibles.add(ataques);
            }
        }

        if (ataquesDisponibles.isEmpty()){
            return listaAtaques.get(0);
        }

        return ataquesDisponibles.get((int)(Math.random() * ataquesDisponibles.size()));
    }

    /*
    Metodo que calcula el daño que recibe el enemigo
     */
    public void recibirDanio(int cantidad) {
        vidaEnemigo = Math.max(0, vidaEnemigo - cantidad);

        //Si la vida es lo suficientemente baja, comprobamos si se cambia de fase
        if (vidaEnemigo <= vidaEnemigoMax / 2 && faseActual == Fase.FASE_1) {
            faseActual = Fase.FASE_2;
        }
    }

   /*
   Metodo que, en función de la fase del enemigo, elige una frase al azar preestablecida
    */
    public String getFraseAtaque() {
        String[] frases;
        if (faseActual == Fase.FASE_1) {
            frases = frasesFase1;
        } else {
            frases = frasesFase2;
        }
        return frases[(int)(Math.random() * frases.length)];
    }

    /*
    Metodo que comprueba si el enemigo aún tiene vida o no
     */
    public boolean estaVivo() {
        return vidaEnemigo > 0;
    }

    /*
    Metodo que indica el cambio de fase del enemigo
    Es utilizado por la clase EscenaCombate para mostrar mensaje de transición entre fases
     */
    public boolean cambioFase() {
        if (faseActual == Fase.FASE_2 && vidaEnemigo <= vidaEnemigoMax/2){
            this.setVelocidadJefe(70);
            return true;
        }
        return false;
    }

    public float getPorcentajeHp() {
        return (float) vidaEnemigo / vidaEnemigoMax;
    }

    public Fase getFase(){
        return faseActual;
    }
    public int getVidaEnemigo(){
    return vidaEnemigo;
    }
    public int getVidaEnemigoMax(){
        return vidaEnemigoMax;
    }

    public void setVelocidadJefe(int velocidadJefe) {
        this.velocidadJefe = velocidadJefe;
    }
}
