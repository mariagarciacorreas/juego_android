package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;

import com.riberadeltajo.proyectoparlamon.mapa.GestorControles;
import com.riberadeltajo.proyectoparlamon.mapa.TipoControl;

/**
 * GestorHUD actúa como la capa controladora central de la Interfaz de Usuario (HUD).
 * Coordina los botones de la pantalla, gestiona el estado de pausa del juego y
 * actúa como puente (Listener) para actualizar los esquemas de control activos.
 */
public class GestorHUD implements OverlayAjustes.OnControlSeleccionado{

    //componentes visuales
    private final BotonHUD botonPausa, botonAjustes;
    private final OverlayPausa overlayPausa;
    private final OverlayAjustes overlayAjustes;

    //referencias de control y estado
    private final GestorControles gestorControles;
    private boolean pausado = false;


    /**
     * Constructor: Calcula de forma dinámica la posición de los componentes en base a la
     * resolución de pantalla y su densidad de píxeles (DPI) para evitar solapamientos.
     */
    public GestorHUD(float anchoPantalla, float altoPantalla, float densidadPantalla, GestorControles gestorControles) {
        this.gestorControles = gestorControles;

        // Escalado por densidad: convierte píxeles abstractos (dp) en píxeles físicos reales (px)
        float radio = 30f * densidadPantalla;
        float margen = 16f * densidadPantalla;

        // Coordenada Y común para alinear los botones horizontalmente en el top
        float cy = margen + radio;

        // Ubicación de derecha a izquierda en la barra superior
        float cxAjustes = anchoPantalla - margen - radio;
        float cxPausa = cxAjustes - radio * 2f - margen;

        // Instanciación de los botones con su respectivo glifo/icono de texto
        botonAjustes = new BotonHUD(cxAjustes, cy, radio, "⚙");
        botonPausa = new BotonHUD(cxPausa,   cy, radio, "⏸");

        // Inicialización de los menús emergentes
        overlayPausa = new OverlayPausa();
        overlayAjustes = new OverlayAjustes();

        // Enlace del patrón observador: este gestor escuchará los eventos de la ventana de ajustes
        overlayAjustes.setListener(this);
        // Sincroniza el menú visual con el tipo de control que el motor tiene cargado por defecto
        overlayAjustes.setSeleccion(gestorControles.getTipoControl());
    }

    /**
     * Actualiza las animaciones internas o lógicas de los overlays activos.
     */
    public void actualizar(){
        overlayPausa.actualizar();
    }

    /**
     * Dibuja de manera secuencial los componentes del HUD en el Canvas.
     * El orden respeta la jerarquía de capas (los overlays cubren a los botones).
     */
    public void dibujar(Canvas canvas) {
        botonPausa.dibujar(canvas);
        botonAjustes.dibujar(canvas);
        overlayPausa.dibujar(canvas);
        overlayAjustes.dibujar(canvas);
    }

    /**
     * Captura los inputs táctiles de la escena y distribuye el foco de interacción.
     * Devuelve true si el HUD absorbió el toque para evitar que se mueva el personaje de fondo.
     */
    public boolean onTouch(float x, float y) {
        // Prioridad 1: Si el panel de ajustes está abierto, él tiene el foco exclusivo
        if (overlayAjustes.isVisible()) {
            overlayAjustes.onTouch(x, y);
            return true;
        }
        // Prioridad 2: Comprobación de clic sobre los botones principales
        if (botonAjustes.dentro(x, y)) {
            overlayAjustes.mostrar();
            return true;
        }
        if (botonPausa.dentro(x, y)) {
            togglePausa();
            return true;
        }
        // Prioridad 3: Cortafuegos defensivo. Bloquea cualquier toque al mapa si el juego está pausado
        if (pausado) {
            return true; //bloquear toques al juego mientras está pausado
        }
        return false; // El toque no pertenece al HUD; se propaga al mapa o los joysticks
    }

    /**
     * Informa al bucle principal si la lógica global del mundo debe congelarse.
     * @return true si el juego está en pausa o si el menú de ajustes está desplegado.
     */
    public boolean isPausado(){
        return pausado || overlayAjustes.isVisible();
    }

    /**
     * Método Callback de la interfaz OnControlSeleccionado.
     * Se ejecuta de manera asíncrona cuando el usuario cambia el esquema en OverlayAjustes.
     */
    @Override
    public void onControlSeleccionado(TipoControl tipo) {
        gestorControles.setTipoControl(tipo);
        overlayAjustes.setSeleccion(tipo);
    }

    /**
     * Alterna de forma segura el estado de congelación del juego, forzando la limpieza de inputs.
     */
    private void togglePausa() {
        pausado = !pausado;
        if (pausado) {
            overlayPausa.mostrar();
            gestorControles.resetearControles();
        }else {
            overlayPausa.ocultar();
        }
    }

}
