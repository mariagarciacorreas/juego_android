package com.riberadeltajo.proyectoparlamon.mapa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.riberadeltajo.proyectoparlamon.R;

import java.util.Map;

/**
 * GestorControles unifica la gestión de las entradas de usuario en dispositivos táctiles.
 * Implementa un sistema de ruteo multitáctil capaz de alternar dinámicamente entre un
 * Joystick analógico o una Cruceta de Flechas (D-Pad), abstrayendo los deltas de dirección (dx, dy).
 */
public class GestorControles {

    // Esquema de controlo por defecto
    private TipoControl tipoControl = TipoControl.JOYSTICK;

    private final Joystick joystick;

    public final Control flechaArriba;
    public final Control flechaAbajo;
    public final Control flechaIzquierda;
    public final Control flechaDerecha;

    private static final int MAX_POINTERS = 10;
    private final Control[] asignacionFlechas = new Control[MAX_POINTERS];
    private final Paint paintControl = new Paint();


    /**
     * Constructor: Calcula la posición ergonómica del pad (esquina inferior izquierda)
     * basándose en la densidad de píxeles (DPI) para que el tamaño físico sea idéntico en cualquier dispositivo.
     */
    public GestorControles(Context context, float anchoPAntalla, float altoPantalla, float densidadPantalla){

        //float btn = BTN_DP * densidadPantalla;
        float margen = 20 * densidadPantalla;

        //posición controles (inverior izquierda
        float radio  = 60f * densidadPantalla;
        float centroX = margen + radio + (int)(48f * densidadPantalla);
        float centroY = altoPantalla - margen - radio - (int)(48f * densidadPantalla);

        //posición joystick
        joystick = new Joystick(centroX, centroY, radio);

        // Configuración de la cruceta (D-Pad)
        int tamFlecha = (int)(48f * densidadPantalla); // 48dp escalados
        float sep = tamFlecha - margen / 2f; //separación de las flechas
        flechaArriba= new Control(
                context,
                centroX - tamFlecha / 2f,
                centroY - sep - tamFlecha,
                R.drawable.flecha_up, tamFlecha);

        flechaAbajo= new Control(
                context,
                centroX - tamFlecha / 2f,
                centroY + sep,
                R.drawable.flecha_down, tamFlecha);

        flechaIzquierda = new Control(
                context,
                centroX - sep - tamFlecha,
                centroY - tamFlecha / 2f,
                R.drawable.flecha_izda, tamFlecha);

        flechaDerecha= new Control(
                context,
                centroX + sep,
                centroY - tamFlecha / 2f,
                R.drawable.flecha_dcha, tamFlecha);

    }

    /**
     * Procesador central (Filtro Multitáctil de Android):
     * Descompone los eventos complejos empaquetados de Android y distribuye las coordenadas
     * al componente de hardware virtual correspondiente.
     */
    public void procesarEvento(MotionEvent event){
        // Extrae la acción pura (ignora el índice del dedo para operaciones globales)
        int accion = event.getActionMasked();

        // Extrae el índice del puntero que gatilló el evento actual
        int idx = event.getActionIndex();

        // Convierte el índice en un Pointer ID persistente (no cambia aunque se levanten otros dedos)
        int pid = event.getPointerId(idx);

        // Coordenadas del evento de este dedo en particular
        float px = event.getX(idx);
        float py = event.getY(idx);

        boolean usaJ = tipoControl == TipoControl.JOYSTICK;
        boolean usaF = tipoControl == TipoControl.FLECHAS;

        switch (accion) {
            // Primer dedo toca la pantalla (DOWN) o dedos subsiguientes se incorporan (POINTER_DOWN)
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // Cortafuegos condicional: Si el joystick está activo y consume el toque, no procesamos flechas.
                // Si falla el joystick y estamos en modo Cruceta, ruteamos el dedo a las flechas.
                if (!(usaJ && joystick.intentarActivar(pid, px, py)) && usaF) {
                    asignarFlecha(pid, px, py);
                }
                break;

            // Cualquier dedo se arrastra por la pantalla
            case MotionEvent.ACTION_MOVE:
                // ACTION_MOVE agrupa los datos de TODOS los dedos activos a la vez. Hay que iterar el array de punteros.
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int p = event.getPointerId(i);
                    float mx = event.getX(i);
                    float my = event.getY(i);

                    if (usaJ) joystick.mover(p, mx, my);

                    // Si el dedo 'p' estaba asignado previamente a una flecha, actualizamos su colisión por si se ha movido fuera
                    if (usaF && p < MAX_POINTERS && asignacionFlechas[p] != null) {
                        asignarFlecha(p, mx, my);
                    }
                }
                break;

            // El último dedo se levanta (UP), un dedo intermedio se retira (POINTER_UP) o el sistema cancela el gesto (CANCEL)
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                joystick.soltar(pid); // Si era el dedo del joystick, se libera (Efecto muelle)
                liberarFlecha(pid); // Si era el dedo de una flecha, limpia su mapeo
                break;
        }

        // Sincroniza los booleanos internos de pulsación de los botones en base a la matriz de dedos
        if (usaF) recalcularFlechas();
    }


    /**
     * Calcula la componente horizontal unitaria ($dx$) del movimiento para el PersonajeMapa.
     * @return Valor entre -1.0 (Izquierda), 0.0 (Neutro) y 1.0 (Derecha).
     */
    public float getDx() {
        if (tipoControl == TipoControl.JOYSTICK && joystick.isActivo()) return joystick.getDx();
        if (tipoControl == TipoControl.FLECHAS) {
            float d = 0f;
            if (flechaIzquierda.pulsado) d -= 1f;
            if (flechaDerecha.pulsado) d += 1f;
            return d;
        }
        return 0f;
    }

    /**
     * Calcula la componente vertical unitaria ($dy$) del movimiento para el PersonajeMapa.
     * @return Valor entre -1.0 (Arriba), 0.0 (Neutro) y 1.0 (Abajo).
     */
    public float getDy() {
        if (tipoControl == TipoControl.JOYSTICK && joystick.isActivo()) return joystick.getDy();
        if (tipoControl == TipoControl.FLECHAS) {
            float d = 0f;
            if (flechaArriba.pulsado) d -= 1f;
            if (flechaAbajo.pulsado) d += 1f;
            return d;
        }
        return 0f;
    }



    /**
     * Renderiza en el Canvas únicamente el esquema de control que esté configurado como activo.
     */
    public void dibujar(Canvas canvas){
        if (tipoControl == TipoControl.JOYSTICK)  {
            joystick.dibujar(canvas);
        }
        if (tipoControl == TipoControl.FLECHAS) {
            flechaArriba.dibujar(canvas, paintControl);
            flechaAbajo.dibujar(canvas, paintControl);
            flechaIzquierda.dibujar(canvas, paintControl);
            flechaDerecha.dibujar(canvas, paintControl);
        }
    }

    /**
     * Resetea por completo los inputs del hardware. Evita el molesto bug del "personaje fantasma"
     * (cuando abres un menú de pausa mientras caminas y el personaje se queda moviéndose solo).
     */
    public void resetearControles(){
        joystick.soltar(joystick.getPointerIdActivo());
        flechaArriba.pulsado = flechaAbajo.pulsado = flechaIzquierda.pulsado = flechaDerecha.pulsado = false;
        for (int i = 0; i < MAX_POINTERS; i++) asignacionFlechas[i] = null;
    }

    /**
     * Vincula el Pointer ID de un dedo específico a la flecha con la que está colisionando físicamente.
     */
    private void asignarFlecha(int pid, float px, float py) {
        if (pid >= MAX_POINTERS) return;
        if (flechaArriba.dentro(px, py)) asignacionFlechas[pid] = flechaArriba;
        else if (flechaAbajo.dentro(px, py)) asignacionFlechas[pid] = flechaAbajo;
        else if (flechaIzquierda.dentro(px, py)) asignacionFlechas[pid] = flechaIzquierda;
        else if (flechaDerecha.dentro(px, py)) asignacionFlechas[pid] = flechaDerecha;
        else asignacionFlechas[pid] = null;
    }

    /**
     * Rompe la asociación del dedo liberado en la matriz de seguimiento.
     */
    private void liberarFlecha(int pid) {
        if (pid < MAX_POINTERS) asignacionFlechas[pid] = null;
    }

    /**
     * Evalúa la matriz completa de dedos y actualiza el flag de renderizado e input de las flechas.
     * Permite soportar la pulsación simultánea de dos flechas a la vez (p. ej. Arriba + Derecha = Diagonal).
     */
    private void recalcularFlechas() {
        flechaArriba.pulsado = flechaAbajo.pulsado = flechaIzquierda.pulsado = flechaDerecha.pulsado = false;
        for (int i = 0; i < MAX_POINTERS; i++) {
            if (asignacionFlechas[i] != null) asignacionFlechas[i].pulsado = true;
        }
    }

    public TipoControl getTipoControl() {
        return tipoControl;
    }

    public void setTipoControl(TipoControl tipoControl) {
        this.tipoControl = tipoControl;
    }
}
