package com.riberadeltajo.proyectoparlamon.dialogos;

import java.util.List;

/**
 * Clase encargada de simular efecto de máquina de escribir para mostrar textos
 * y diálogos letra por letra.
 */
public class EscritorTexto {

    private List<String> lineas;
    private String[] lineasVisibles;
    private int lineaActual;
    private int indiceActual; //indica por qué letra va
    private long tiempoUltimaLetra;
    private long velocidad; //milisegundos entre letra y letra
    private boolean terminado;

    /**
     * Constructor para inicializar el escritor con una velocidad en milisegundos,
     * que es el tiempo en que tarda en aparecer una nueva letra.
     * @param velocidad
     */
    public EscritorTexto(long velocidad){
        this.velocidad = velocidad;
        this.terminado = true;
    }

    /**
     * Prepara e inicia un nuevo proceso de escritura para un conjunto de líneas
     * almacenadas en el array que se pasa por parámetro.
     * @param lineas
     */
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

    /**
     * Lógica principal para avanzar en la escritura del texto.
     * Debe ser llamado en el bucle principal de juego para una actualización constante.
     */
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

    /**
     * completar escritura del texto completo de un solo golpe
     */
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
