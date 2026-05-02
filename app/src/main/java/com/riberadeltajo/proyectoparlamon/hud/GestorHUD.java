package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;

import com.riberadeltajo.proyectoparlamon.mapa.GestorControles;
import com.riberadeltajo.proyectoparlamon.mapa.TipoControl;

public class GestorHUD implements OverlayAjustes.OnControlSeleccionado{

    private final BotonHUD botonPausa, botonAjustes;
    private final OverlayPausa overlayPausa;
    private final OverlayAjustes overlayAjustes;
    private final GestorControles gestorControles;
    private boolean pausado = false;


    public GestorHUD(float anchoPantalla, float altoPantalla, float densidadPantalla, GestorControles gestorControles) {
        this.gestorControles = gestorControles;

        float radio = 30f * densidadPantalla;
        float margen = 16f * densidadPantalla;
        float cy = margen + radio;
        float cxAjustes = anchoPantalla - margen - radio;
        float cxPausa = cxAjustes - radio * 2f - margen;

        botonAjustes = new BotonHUD(cxAjustes, cy, radio, "⚙");
        botonPausa = new BotonHUD(cxPausa,   cy, radio, "⏸");

        overlayPausa = new OverlayPausa();
        overlayAjustes = new OverlayAjustes();
        overlayAjustes.setListener(this);
        overlayAjustes.setSeleccion(gestorControles.getTipoControl());
    }

    public void actualizar(){
        overlayPausa.actualizar();
    }

    public void dibujar(Canvas canvas) {
        botonPausa.dibujar(canvas);
        botonAjustes.dibujar(canvas);
        overlayPausa.dibujar(canvas);
        overlayAjustes.dibujar(canvas);
    }

    public boolean onTouch(float x, float y) {
        if (overlayAjustes.isVisible()) {
            overlayAjustes.onTouch(x, y);
            return true;
        }
        if (botonAjustes.dentro(x, y)) {
            overlayAjustes.mostrar();
            return true;
        }
        if (botonPausa.dentro(x, y)) {
            togglePausa();
            return true;
        }
        if (pausado) {
            return true; //bloquear toques al juego mientras está pausado
        }
        return false;
    }

    public boolean isPausado(){
        return pausado || overlayAjustes.isVisible();
    }

    @Override
    public void onControlSeleccionado(TipoControl tipo) {
        gestorControles.setTipoControl(tipo);
        overlayAjustes.setSeleccion(tipo);
    }

    private void togglePausa() {
        pausado = !pausado;
        if (pausado) {
            overlayPausa.mostrar(); gestorControles.resetearControles();
        }else {
            overlayPausa.ocultar();
        }
    }

}
