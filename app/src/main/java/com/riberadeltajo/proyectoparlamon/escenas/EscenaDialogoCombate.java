package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.combate.Jefe;
import com.riberadeltajo.proyectoparlamon.combate.Jugador;
import com.riberadeltajo.proyectoparlamon.dialogos.DialogosJefe;
import com.riberadeltajo.proyectoparlamon.dialogos.EscritorTexto;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

import java.util.List;

/**
 * Cutscene de encuentro con Ciber Franco, previa al combate.
 * Reutiliza EscritorTexto igual que EscenaInicio.
 */
public class EscenaDialogoCombate implements Escena {

    private final Context context;
    private final GestorEscenas gestorEscenas;
    private final Jugador jugador;

    private final EscritorTexto escritor;
    private final List<List<String>> dialogos;
    private int pantallaActual = 0;

    // Sprite de Ciber Franco
    private Bitmap imgCiberFranco;
    private int alphaCiberFranco = 0;

    // Paleta (igual que el resto del juego)
    private final Paint paintTexto;
    private final Paint paintNombre;  // Para "CIBER FRANCO:" en amarillo
    private final Paint paintPista;

    private float escalaPista = 1f;
    private float tiempoPulsacion = 0f;

    public EscenaDialogoCombate(Context context, GestorEscenas gestorEscenas,
                                String nombreJugador, String claseJugador) {
        this.context        = context;
        this.gestorEscenas  = gestorEscenas;
        this.jugador        = new Jugador(nombreJugador, claseJugador);

        this.dialogos = DialogosJefe.getDialogos();
        this.escritor = new EscritorTexto(45); // un poco más rápido que el intro

        // Paint texto normal (blanco, monospace)
        paintTexto = new Paint();
        paintTexto.setColor(Color.WHITE);
        paintTexto.setTextSize(35);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setAntiAlias(false);

        // Paint para líneas que empiezan por "CIBER FRANCO:" (amarillo)
        paintNombre = new Paint();
        paintNombre.setColor(Color.rgb(255, 235, 59));
        paintNombre.setTextSize(35);
        paintNombre.setTypeface(Typeface.MONOSPACE);
        paintNombre.setAntiAlias(false);

        // Paint pista
        paintPista = new Paint();
        paintPista.setColor(Color.GRAY);
        paintPista.setTextSize(30);
        paintPista.setTypeface(Typeface.MONOSPACE);
        paintPista.setAntiAlias(false);

        // Imagen Ciber Franco (ya existe en drawable)
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.ciberfrank_intro);
        int ancho = 500;
        int alto  = (int)(original.getHeight() * (ancho / (float) original.getWidth()));
        imgCiberFranco = Bitmap.createScaledBitmap(original, ancho, alto, true);
        original.recycle();

        cargarPantalla(0);
    }

    // ─── Jugador accesible desde EscenaCombate ───────────────────────────────

    public Jugador getJugador() { return jugador; }

    // ─── Lógica interna ──────────────────────────────────────────────────────

    private void cargarPantalla(int index) {
        pantallaActual = index;
        escritor.iniciarEscritura(dialogos.get(index));
        // Fade-in del sprite a partir de la pantalla 1 (cuando habla Franco)
        if (index >= 1) {
            alphaCiberFranco = Math.min(255, alphaCiberFranco + 80);
        }
    }

    @Override
    public void actualizar() {
        escritor.actualizarEscritura();

        // Fade-in del sprite en pantalla 0
        if (pantallaActual == 0 && escritor.getLineaActual() >= 5 && alphaCiberFranco < 255) {
            alphaCiberFranco = Math.min(255, alphaCiberFranco + 6);
        }

        // Animación pista
        if (escritor.isTerminado()) {
            tiempoPulsacion += 0.05f;
            escalaPista = 1f + 0.08f * (float) Math.sin(tiempoPulsacion);
        }
    }

    @Override
    public void renderizar(Canvas canvas) {

        if (canvas == null) return;

        canvas.drawColor(Color.DKGRAY);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        // Sprite de Ciber Franco (derecha de la pantalla)
        if (alphaCiberFranco > 0) {
            Paint p = new Paint();
            p.setAlpha(alphaCiberFranco);
            float x = w - imgCiberFranco.getWidth() - 60;
            float y = h / 2f - imgCiberFranco.getHeight() / 2f;
            canvas.drawBitmap(imgCiberFranco, x, y, p);
        }

        // Texto del diálogo
        String[] lineas = escritor.getLineasVisibles();
        float lineHeight = paintTexto.getTextSize() + 18;
        float margen = 60f;
        float pistaY = h - margen;
        float alturaBloqueTexto = lineas.length * lineHeight;
        float textoY = pistaY - paintPista.getTextSize() - 40 - alturaBloqueTexto;

        float y = textoY;
        for (String linea : lineas) {
            // Si la línea es "CIBER FRANCO:" o empieza por comilla, color diferente
            boolean esFranco = linea.startsWith("CIBER FRANCO");
            canvas.drawText(linea, 100, y, esFranco ? paintNombre : paintTexto);
            y += lineHeight;
        }

        // Pista "▼ Pulsa para continuar"
        if (escritor.isTerminado()) {
            String textoPista = "▼ Pulsa para continuar";
            float anchoPista = paintPista.measureText(textoPista);
            float ejeX = w - anchoPista - margen;
            float ejeY = pistaY;

            canvas.save();
            canvas.translate(ejeX + anchoPista / 2f, ejeY);
            canvas.scale(escalaPista, escalaPista);
            canvas.translate(-(ejeX + anchoPista / 2f), -ejeY);
            canvas.drawText(textoPista, ejeX, ejeY, paintPista);
            canvas.restore();
        }
    }

    @Override
    public void onTouch(float x, float y) {
        if (!escritor.isTerminado()) {
            // Completar texto al instante
            escritor.completarEscritura();
            alphaCiberFranco = 255;
        } else {
            int siguiente = pantallaActual + 1;
            if (siguiente < dialogos.size()) {
                cargarPantalla(siguiente);
            } else {
                // ¡A combatir!
                gestorEscenas.cambiarEscena(
                        new EscenaCombate(context, gestorEscenas, jugador, new Jefe())
                );
            }
        }
    }
}
