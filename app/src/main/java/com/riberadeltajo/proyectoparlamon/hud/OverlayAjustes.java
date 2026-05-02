package com.riberadeltajo.proyectoparlamon.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.mapa.TipoControl;

//panel de ajustes superpuesto para elegir el tipo de control con el que jugar
public class OverlayAjustes {

    public interface OnControlSeleccionado{
        void onControlSeleccionado(TipoControl tipo);
    }

    private boolean visible = false;
    private TipoControl seleccion = TipoControl.FLECHAS;
    private OnControlSeleccionado listener;

    private static final TipoControl[] OPCIONES = { TipoControl.JOYSTICK, TipoControl.FLECHAS};
    private static final String[] ETIQUETAS = { "Joystick", "Flechas"};
    private static final String[] ICONOS = { "🕹", "🎮"};

    private RectF panelRect, botonCerrar;
    private RectF[] botonesOpciones;
    private boolean calculado = false;
    private float w, h;

    private final Paint paintFondo, paintPanel, paintBordePanel,
            paintBtnActivo, paintBtnInactivo, paintBordeBtn,
            paintTexto, paintIcono, paintTitulo, paintCerrar;

    public OverlayAjustes() {
        paintFondo = new Paint(); paintFondo.setColor(Color.argb(120, 0, 0, 0));

        paintPanel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPanel.setColor(Color.rgb(15, 15, 25)); paintPanel.setStyle(Paint.Style.FILL);

        paintBordePanel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBordePanel.setColor(Color.rgb(255, 235, 59));
        paintBordePanel.setStyle(Paint.Style.STROKE); paintBordePanel.setStrokeWidth(2f);

        paintBtnActivo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBtnActivo.setColor(Color.rgb(255, 235, 59)); paintBtnActivo.setStyle(Paint.Style.FILL);

        paintBtnInactivo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBtnInactivo.setColor(Color.rgb(40, 40, 55)); paintBtnInactivo.setStyle(Paint.Style.FILL);

        paintBordeBtn = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBordeBtn.setColor(Color.argb(180, 255, 235, 59));
        paintBordeBtn.setStyle(Paint.Style.STROKE); paintBordeBtn.setStrokeWidth(1.5f);

        paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTexto.setTypeface(Typeface.MONOSPACE); paintTexto.setTextAlign(Paint.Align.CENTER);

        paintIcono = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIcono.setTextAlign(Paint.Align.CENTER);

        paintTitulo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTitulo.setColor(Color.WHITE); paintTitulo.setTextSize(36f);
        paintTitulo.setTypeface(Typeface.MONOSPACE); paintTitulo.setTextAlign(Paint.Align.CENTER);

        paintCerrar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCerrar.setColor(Color.rgb(200, 80, 80)); paintCerrar.setTextSize(36f);
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

    public boolean onTouch(float px, float py) {
        if (!visible || !calculado) {
            return false;
        }
        if (!panelRect.contains(px, py)) {
            ocultar(); return true;
        }
        if (botonCerrar.contains(px, py)) {
            ocultar(); return true;
        }
        if (botonesOpciones != null) {
            for (int i = 0; i < botonesOpciones.length; i++) {
                if (botonesOpciones[i].contains(px, py)) {
                    seleccion = OPCIONES[i];
                    if (listener != null) {
                        listener.onControlSeleccionado(seleccion);
                    }
                    ocultar(); return true;
                }
            }
        }
        return true;
    }

    public void dibujar(Canvas canvas) {
        if (!visible) return;
        w = canvas.getWidth(); h = canvas.getHeight();
        calcularSiNecesario();

        canvas.drawRect(0, 0, w, h, paintFondo);
        canvas.drawRoundRect(panelRect, 20, 20, paintPanel);
        canvas.drawRoundRect(panelRect, 20, 20, paintBordePanel);
        canvas.drawText("⚙ Tipo de control", w / 2f, panelRect.top + 60, paintTitulo);
        canvas.drawText("✕", botonCerrar.centerX(), botonCerrar.centerY() + 14, paintCerrar);

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

    private void calcularSiNecesario() {
        if (calculado) return;

        float pW = w * 0.55f, pH = h * 0.55f;
        float pL = w / 2f - pW / 2f, pT = h / 2f - pH / 2f;
        panelRect = new RectF(pL, pT, pL + pW, pT + pH);

        float cr = 22f;
        botonCerrar = new RectF(panelRect.right - cr * 2.5f, panelRect.top + 10,
                panelRect.right - 10, panelRect.top + cr * 2.5f);

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
