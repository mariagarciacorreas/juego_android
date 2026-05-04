package com.riberadeltajo.proyectoparlamon.combate;

public class Combate {

    public enum Estado {
        ESPERAR_JUGADOR,
        TURNO_JUGADOR,
        TURNO_ENEMIGO,
        FIN_VICTORIA,
        FIN_DERROTA
    }

    private Estado estado;
    private Jugador jugador;
    private Jefe enemigo;

    private String mensajeTurno = "";
    private int danioTurnoPasado = 0;
    private boolean cambiarFase = false;

    // Delay entre turno del jugador y turno del enemigo (ms)
    private static final long DELAY_TURNO_ENEMIGO_MS = 1800;
    private long tiempoFinTurnoJugador = Long.MAX_VALUE;

    public Combate(Jugador jugador, Jefe enemigo) {
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.estado  = Estado.ESPERAR_JUGADOR;
    }

    public void actualizar() {
        if (estado == Estado.TURNO_JUGADOR) {
            long momento = System.currentTimeMillis();
            if (momento - tiempoFinTurnoJugador >= DELAY_TURNO_ENEMIGO_MS) {
                ejecutarTurnoEnemigo();
            }
        }
    }

    public void jugadorAtaca(int indiceAtaque) {
        if (estado != Estado.ESPERAR_JUGADOR) {
            return;
        }

        Ataques ataque = jugador.getAtaques().get(indiceAtaque);

        if (!ataque.usoAtaquePosible()){
            mensajeTurno = "¡" + ataque.getNombre() + " no tiene usos restantes!";
            return;
        }

        ataque.usarAtaque();

        if (!ataque.ataqueGolpea()){
            mensajeTurno = "Usas " + ataque.getNombre() + "...\n¡Pero fallas el ataque!";
            estado = Estado.TURNO_JUGADOR;
            tiempoFinTurnoJugador = System.currentTimeMillis();
            return;
        }

        int danio = ataque.atacar();
        enemigo.recibirDanio(danio);
        danioTurnoPasado = danio;

        cambiarFase = (enemigo.getFase() == Jefe.Fase.FASE_2
                && danio >= enemigo.getVidaEnemigoMax() / 2
                && enemigo.getVidaEnemigo() <= enemigo.getVidaEnemigoMax() / 2);

        if (!enemigo.estaVivo()) {
            mensajeTurno = "¡" + ataque.getNombre() + "! Causas " + danio + " de daño.\n¡CIBER FRANCO CAE DERROTADO!";
            estado = Estado.FIN_VICTORIA;
        } else {
            String extraFase2;
            if (cambiarFase) {
                extraFase2 = "\n⚠ ¡CIBER FRANCO ENTRA EN FASE 2!";
            } else {
                extraFase2 = "";
            }
            mensajeTurno = "Usas " + ataque.getNombre() + ".\n¡" + danio + " de daño!" + extraFase2;
            estado = Estado.TURNO_JUGADOR;
            tiempoFinTurnoJugador = System.currentTimeMillis();
        }
    }

    private void ejecutarTurnoEnemigo() {
        estado = Estado.TURNO_ENEMIGO;
        Ataques ataque = enemigo.elegirAtaque();
        String frase  = enemigo.getFraseAtaque();
        int danio = ataque.atacar();
        jugador.recibirDanio(danio);
        danioTurnoPasado = danio;

        if (!jugador.estaVivo()) {
            mensajeTurno = frase + "\n¡" + ataque.getNombre() + "! " + danio + " de daño.\n¡Has caído...!";
            estado = Estado.FIN_DERROTA;
        } else {
            mensajeTurno = frase + "\n¡" + ataque.getNombre() + "! Te hace " + danio + " de daño.";
            estado = Estado.ESPERAR_JUGADOR;
        }
    }

    public Estado getEstado(){
        return estado;
    }
    public String getMensajeTurno(){
        return mensajeTurno;
    }
    public int getDanioUltimoTurno(){
        return danioTurnoPasado;
    }
    public boolean isEntroEnFase2(){
        return cambiarFase;
    }
    public Jugador getJugador(){
        return jugador;
    }
    public Jefe getEnemigo(){
        return enemigo;
    }
}

