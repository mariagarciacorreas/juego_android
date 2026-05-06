package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.mapa.TipoControl;

/**
 * OverlayAjustes representa un menú modal superpuesto que permite al usuario
 * configurar el esquema de controles (Joystick o Flechas) en tiempo de ejecución.
 */public class OverlayAjustes {

    /**
     * Interfaz de comunicación para notificar a otras clases (como el GestorHUD)
     * cuando el usuario ha cambiado su preferencia de control.
     */
    public interface OnControlSeleccionado{
        void onControlSeleccionado(TipoControl tipo);
    }

    //estado y lógica
    private boolean visible = false;
    private TipoControl seleccion = TipoControl.FLECHAS;
    private OnControlSeleccionado listener;

    //opciones visuales
    private static final TipoControl[] OPCIONES = { TipoControl.JOYSTICK, TipoControl.FLECHAS};
    private static final String[] ETIQUETAS = { "Joystick", "Flechas"};
    private static final String[] ICONOS = { "🕹", "🎮"};

    //geometría visual de la interfaz
    private RectF panelRect, botonCerrar;
    private RectF[] botonesOpciones;
    private boolean calculado = false;
    private float w, h;

    //pinceles
    private final Paint paintFondo, paintPanel, paintBordePanel,
            paintBtnActivo, paintBtnInactivo, paintBordeBtn,
            paintTexto, paintIcono, paintTitulo, paintCerrar;

    /**
     * Constructor: Inicializa la paleta de colores y estilos siguiendo una estética
     * oscura con acentos amarillos (Cyberpunk/Retro).
     */
    public OverlayAjustes() {
        // Capa de oscurecimiento del fondo
        paintFondo = new Paint();
        paintFondo.setColor(Color.argb(120, 0, 0, 0));

        // Estilo del cuerpo del panel
        paintPanel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPanel.setColor(Color.rgb(15, 15, 25));
        paintPanel.setStyle(Paint.Style.FILL);

        // Borde dorado brillante para el panel
        paintBordePanel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBordePanel.setColor(Color.rgb(255, 235, 59));
        paintBordePanel.setStyle(Paint.Style.STROKE);
        paintBordePanel.setStrokeWidth(2f);

        // Colores para estados de los botones (Seleccionado vs No seleccionado)
        paintBtnActivo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBtnActivo.setColor(Color.rgb(255, 235, 59));
        paintBtnActivo.setStyle(Paint.Style.FILL);

        paintBtnInactivo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBtnInactivo.setColor(Color.rgb(40, 40, 55));
        paintBtnInactivo.setStyle(Paint.Style.FILL);

        paintBordeBtn = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBordeBtn.setColor(Color.argb(180, 255, 235, 59));
        paintBordeBtn.setStyle(Paint.Style.STROKE);
        paintBordeBtn.setStrokeWidth(1.5f);

        // Tipografías
        paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTextAlign(Paint.Align.CENTER);

        paintIcono = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIcono.setTextAlign(Paint.Align.CENTER);

        paintTitulo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTitulo.setColor(Color.WHITE); paintTitulo.setTextSize(36f);
        paintTitulo.setTypeface(Typeface.MONOSPACE);
        paintTitulo.setTextAlign(Paint.Align.CENTER);

        paintCerrar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCerrar.setColor(Color.rgb(200, 80, 80));
        paintCerrar.setTextSize(36f);
        paintCerrar.setTextAlign(Paint.Align.CENTER);
    }

    public void setListener(OnControlSeleccionado l) {
        listener = l;
    }
    public void setSeleccion(TipoControl t) {
        seleccion = t;
    }
    public void mostrar() {
        visible = true;
    }
    public void ocultar() {
        visible = false;
    }
    public boolean isVisible() {
        return visible;
    }

    /**
     * Procesa los eventos táctiles. Devuelve true si el toque fue "consumido" por el menú,
     * impidiendo que el toque afecte a elementos que están debajo (como el movimiento del personaje).
     */
    public boolean onTouch(float px, float py) {
        if (!visible || !calculado) {
            return false;
        }
        // Si el usuario toca fuera del panel central, el menú se cierra automáticamente
        if (!panelRect.contains(px, py)) {
            ocultar(); return true;
        }
        // Clic en el botón "X" de cierre
        if (botonCerrar.contains(px, py)) {
            ocultar(); return true;
        }
        // Comprobar si se ha pulsado alguno de los botones de opción
        if (botonesOpciones != null) {
            for (int i = 0; i < botonesOpciones.length; i++) {
                if (botonesOpciones[i].contains(px, py)) {
                    seleccion = OPCIONES[i];
                    // Notifica al sistema del nuevo control seleccionado
                    if (listener != null) {
                        listener.onControlSeleccionado(seleccion);
                    }
                    ocultar(); return true;
                }
            }
        }
        return true;
    }

    /**
     * Dibuja el overlay completo sobre el Canvas.
     */
    public void dibujar(Canvas canvas) {
        if (!visible) return;
        w = canvas.getWidth(); h = canvas.getHeight();
        calcularSiNecesario(); // Asegura que las coordenadas estén adaptadas a la resolución actual

        canvas.drawRect(0, 0, w, h, paintFondo);
        canvas.drawRoundRect(panelRect, 20, 20, paintPanel);
        canvas.drawRoundRect(panelRect, 20, 20, paintBordePanel);
        canvas.drawText("⚙ Tipo de control", w / 2f, panelRect.top + 60, paintTitulo);
        canvas.drawText("✕", botonCerrar.centerX(), botonCerrar.centerY() + 14, paintCerrar);

        //dibujar dinámicamente cada botón de opción
        for (int i = 0; i < OPCIONES.length; i++) {
            RectF btn = botonesOpciones[i];
            boolean activo = OPCIONES[i] == seleccion;
            canvas.drawRoundRect(btn, 12, 12, activo ? paintBtnActivo : paintBtnInactivo);
            canvas.drawRoundRect(btn, 12, 12, paintBordeBtn);
            paintIcono.setTextSize(btn.height() * 0.35f);
            canvas.drawText(ICONOS[i], btn.centerX(), btn.centerY() - 8, paintIcono);
            paintTexto.setColor(activo ? Color.rgb(20, 20, 30) : Color.WHITE);
            paintTexto.setTextSize(24f);
            canvas.drawText(ETIQUETAS[i], btn.centerX(), btn.centerY() + btn.height() * 0.28f, paintTexto);
        }
    }

    /**
     * Calcula las dimensiones y posiciones de todos los elementos basándose en
     * porcentajes del ancho/alto de la pantalla para garantizar la responsividad.
     */
    private void calcularSiNecesario() {
        if (calculado) return;

        // El panel ocupa el 55% de la pantalla
        float pW = w * 0.55f, pH = h * 0.55f;
        float pL = w / 2f - pW / 2f, pT = h / 2f - pH / 2f;
        panelRect = new RectF(pL, pT, pL + pW, pT + pH);

        // Posición del botón de cerrar (esquina superior derecha interna)
        float cr = 22f;
        botonCerrar = new RectF(panelRect.right - cr * 2.5f, panelRect.top + 10,
                panelRect.right - 10, panelRect.top + cr * 2.5f);

        // Dimensiones de los botones de selección
        float bW = pW * 0.25f, bH = pH * 0.40f, sep = pW * 0.05f;
        float total = OPCIONES.length * bW + (OPCIONES.length - 1) * sep;
        float sx = panelRect.left + (pW - total) / 2f;
        float by = panelRect.top  + pH * 0.42f;

        botonesOpciones = new RectF[OPCIONES.length];
        for (int i = 0; i < OPCIONES.length; i++) {
            float x = sx + i * (bW + sep);
            botonesOpciones[i] = new RectF(x, by, x + bW, by + bH);
        }
        calculado = true;
    }
}
