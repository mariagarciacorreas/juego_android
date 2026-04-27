package com.riberadeltajo.proyectoparlamon.combate;

public class Ataques {

    private String nombre;
    private int potencia;
    private String descripcion;
    private int precisionAtaque;

    private final int usosAtaque;
    private int usosRestantes;


    public Ataques(String nombre, int potencia, String descripcion, int precisionAtaque, int usosAtaque) {
        this.nombre = nombre;
        this.potencia = potencia;
        this.descripcion = descripcion;
        this.precisionAtaque = precisionAtaque;
        this.usosAtaque = usosAtaque;
        this.usosRestantes = usosAtaque;
    }

    /*
    Metodo que devuelve la potencia de un ataque.
    Se utiliza para atacar en sí
     */
    public int atacar() {
        return potencia;
    }

    public boolean ataqueGolpea(){
        return (int) (Math.random() * 100) < precisionAtaque;
    }

    public boolean usoAtaquePosible(){
        return usosRestantes > 0;
    }

    public void usarAtaque(){
        if (usosRestantes > 0){
            usosRestantes--;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getPotencia() {
        return potencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecisionAtaque() {
        return precisionAtaque;
    }

    public int getUsosAtaque() {
        return usosAtaque;
    }

    public int getUsosRestantes() {
        return usosRestantes;
    }
}


