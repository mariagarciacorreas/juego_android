package com.riberadeltajo.proyectoparlamon.dialogos;

import java.util.List;

public class EscritorTexto {

    private List<String> lineas;
    private String[] lineasVisibles;
    private int lineaActual;
    private int indiceActual; //indica por qué letra va
    private long tiempoUltimaLetra;
    private long velocidad; //milisegundos entre letra y letra
    private boolean terminado;

    public EscritorTexto(long velocidad){
        this.velocidad = velocidad;
        this.terminado = true;
    }

    public void iniciarEscritura(List<String> lineas){
        this.lineas = lineas;
        this.lineasVisibles = new String[lineas.size()];
        for (int i = 0; i < lineas.size(); i++) {
            lineasVisibles[i] = "";
        }
        this.lineaActual = 0;
        this.indiceActual = 0;
        this.tiempoUltimaLetra = System.currentTimeMillis();
        this.terminado = false;
    }

    public void actualizarEscritura(){
        if (terminado){
            return;
        }

        long ahora = System.currentTimeMillis();
        if(ahora - tiempoUltimaLetra < velocidad) {
            return;
        }

        tiempoUltimaLetra = ahora;
        String lineaTexto = lineas.get(lineaActual);

        //si la línea está vacía, saltarla
        if(lineaTexto.isEmpty()){
            lineasVisibles[lineaActual] = "";
            lineaActual++;
            indiceActual = 0;
            if(lineaActual >= lineas.size()){
                terminado = true;
            }
            return;
        }

        indiceActual++;
        lineasVisibles[lineaActual] = lineaTexto.substring(0, indiceActual);

        if(indiceActual >= lineaTexto.length()){
            //pasar a la siguiete línea
            lineaActual++;
            indiceActual = 0;
            if(lineaActual >= lineas.size()){
                terminado = true;
            }
        }
    }

    //completar escritura del texto completo de un solo golpe
    public void completarEscritura(){
        for (int i = 0; i < lineas.size(); i++) {
            lineasVisibles[i] = lineas.get(i);
        }
        indiceActual = lineas.size();
        terminado = true;
    }

    public String[] getLineasVisibles() {
        return lineasVisibles;
    }

    public boolean isTerminado(){
        return terminado;
    }

    public int getLineaActual() {
        return lineaActual;
    }

}
