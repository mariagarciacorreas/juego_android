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
        //finalizar si se ha mostrado el texto al completo
        if (terminado){
            return;
        }

        //control de tiempo de escritura
        long ahora = System.currentTimeMillis(); //actualizar frame de tiempo al instante actual
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
            //verificar si se ha llegado a la última línea
            if(lineaActual >= lineas.size()){
                terminado = true;
            }
            return;
        }

        //avanzar un carácter y actualizar subcadena visible
        indiceActual++;
        lineasVisibles[lineaActual] = lineaTexto.substring(0, indiceActual);

        //comprobar si se ha terminado de escribir la línea actual por completo
        if(indiceActual >= lineaTexto.length()){
            //pasar a la siguiete línea
            lineaActual++;
            indiceActual = 0;
            //verificar si la línea actual es la última del texto completo
            if(lineaActual >= lineas.size()){
                terminado = true;
            }
        }
    }


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
