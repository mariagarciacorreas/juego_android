package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.combate.Ataques;
import com.riberadeltajo.proyectoparlamon.combate.Jefe;
import com.riberadeltajo.proyectoparlamon.combate.Combate;
import com.riberadeltajo.proyectoparlamon.combate.Jugador;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

import java.util.List;

/**
 * Pantalla de combate por turnos estilo Pokémon clásico.
 *
 * Layout (horizontal):
 * ┌─────────────────────────────────────────────────────┐
 * │  [Sprite Franco]            [Sprite Jugador]        │  ← zona sprites (60% alto)
 * │  CIBER FRANCO ████░░ HP     JUGADOR ████░░ HP       │
 * ├─────────────────────────────────────────────────────┤
 * │  Mensaje de turno                                   │  ← caja de texto (20% alto)
 * ├────────────┬────────────┬────────────┬──────────────┤
 * │  ATAQUE 1  │  ATAQUE 2  │  ATAQUE 3  │  ATAQUE 4   │  ← botones ataque (20% alto)
 * └────────────┴────────────┴────────────┴──────────────┘
 */
public class EscenaCombate implements Escena {

    private final Context context;
    private final GestorEscenas gestorEscenas;
    private final Combate gestorCombate;
    private final Jugador jugador;
    private final Jefe enemigo;

    // Sprites
    private Bitmap spriteJugador;
    private Bitmap spriteEnemigo;
    private Bitmap fondoJefe;

    // Paints
    private final Paint paintTexto;
    private final Paint paintTextoBtn;
    private final Paint paintMensaje;
    private final Paint paintHpBarra;
    private final Paint paintHpBarraFondo;
    private final Paint paintNombrePersonaje;
    private final Paint paintBtn;
    private final Paint paintBtnSeleccionado;
    private final Paint paintFase2;

    // Zonas táctiles de los botones de ataque
    private RectF[] botonesAtaque;

    // Dimensiones calculadas en el primer renderizado
    private float w, h;
    private boolean dimensionesCalculadas = false;

    // Mensaje que se muestra en la caja de texto inferior
    private String mensajeActual = "¡Tu turno!";

    // Shake del sprite del enemigo al recibir daño
    private float shakeEnemigo = 0f;
    private float shakeJugador = 0f;
    private static final float SHAKE_DURACION = 300f; // ms
    private long tiempoInicioShakeEnemigo = 0;
    private long tiempoInicioShakeJugador = 0;

    // Flash de fase 2
    private boolean mostrarFlashFase2 = false;
    private long tiempoFlashFase2 = 0;
    private static final long DURACION_FLASH = 1200;

    // Último estado del gestor (para detectar cambios)
    private Combate.Estado estadoAnterior = Combate.Estado.ESPERAR_JUGADOR;

    public EscenaCombate(Context context, GestorEscenas gestorEscenas, Jugador jugador, Jefe enemigo) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.gestorCombate = new Combate(jugador, enemigo);

        // ── Paints ──────────────────────────────────────────────────────────
        paintTexto = new Paint();
        paintTexto.setColor(Color.WHITE);
        paintTexto.setTextSize(32);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setAntiAlias(false);

        paintMensaje = new Paint();
        paintMensaje.setColor(Color.WHITE);
        paintMensaje.setTextSize(28);
        paintMensaje.setTypeface(Typeface.MONOSPACE);
        paintMensaje.setAntiAlias(false);

        paintNombrePersonaje = new Paint();
        paintNombrePersonaje.setColor(Color.rgb(255, 235, 59)); // amarillo
        paintNombrePersonaje.setTextSize(30);
        paintNombrePersonaje.setTypeface(Typeface.MONOSPACE);
        paintNombrePersonaje.setAntiAlias(false);

        paintHpBarra = new Paint();
        paintHpBarra.setStyle(Paint.Style.FILL);

        paintHpBarraFondo = new Paint();
        paintHpBarraFondo.setColor(Color.rgb(80, 80, 80));
        paintHpBarraFondo.setStyle(Paint.Style.FILL);

        paintBtn = new Paint();
        paintBtn.setColor(Color.rgb(50, 50, 60));
        paintBtn.setStyle(Paint.Style.FILL);

        paintBtnSeleccionado = new Paint();
        paintBtnSeleccionado.setColor(Color.rgb(70, 70, 100));
        paintBtnSeleccionado.setStyle(Paint.Style.FILL);

        paintTextoBtn = new Paint();
        paintTextoBtn.setColor(Color.WHITE);
        paintTextoBtn.setTextSize(26);
        paintTextoBtn.setTypeface(Typeface.MONOSPACE);
        paintTextoBtn.setAntiAlias(false);

        paintFase2 = new Paint();
        paintFase2.setColor(Color.rgb(255, 60, 60));
        paintFase2.setTextSize(40);
        paintFase2.setTypeface(Typeface.MONOSPACE);
        paintFase2.setAntiAlias(false);

        // Sprite jugador según clase
        cargarSpriteJugador();

        // Sprite enemigo
        Bitmap orig = BitmapFactory.decodeResource(context.getResources(), R.drawable.ciberfrank_intro);
        int ae = 280;
        int be = (int) (orig.getHeight() * (ae / (float) orig.getWidth()));
        spriteEnemigo = Bitmap.createScaledBitmap(orig, ae, be, true);
        orig.recycle();

        Bitmap fondoPeleaJefe = BitmapFactory.decodeResource(context.getResources(), R.drawable.fondo_congreso_v2);
        fondoJefe = fondoPeleaJefe;
    }

    private void cargarSpriteJugador() {
        int spriteJugador;
        switch (jugador.getClase().toLowerCase()) {
            case "guerrero":
                spriteJugador = R.drawable.sprite_guerrero_ejemplo;
                break;
            case "mago":
                spriteJugador = R.drawable.sprite_mago_ejemplo;
                break;
            default:
                spriteJugador = R.drawable.sprite_elfo_ejemplo;
                break;
        }
        Bitmap orig = BitmapFactory.decodeResource(context.getResources(), spriteJugador);
        int aj = 220;
        int bj = (int) (orig.getHeight() * (aj / (float) orig.getWidth()));
        this.spriteJugador = Bitmap.createScaledBitmap(orig, aj, bj, true);
        orig.recycle();
    }

    // ─── Lógica ──────────────────────────────────────────────────────────────

    @Override
    public void actualizar() {
        gestorCombate.actualizar();

        Combate.Estado estadoActual = gestorCombate.getEstado();

        // Detectar transición de estado → actualizar mensaje y shake
        if (estadoActual != estadoAnterior) {
            mensajeActual = gestorCombate.getMensajeTurno();

            if (estadoAnterior == Combate.Estado.ESPERAR_JUGADOR
                    && estadoActual == Combate.Estado.TURNO_JUGADOR) {
                // El jugador atacó → shake en enemigo
                tiempoInicioShakeEnemigo = System.currentTimeMillis();
                // ¿Entró en fase 2?
                if (gestorCombate.isEntroEnFase2()) {
                    mostrarFlashFase2 = true;
                    tiempoFlashFase2 = System.currentTimeMillis();
                }
            }

            if (estadoAnterior == Combate.Estado.TURNO_JUGADOR
                    && estadoActual == Combate.Estado.TURNO_ENEMIGO) {
                // El enemigo ataca → shake en jugador
                tiempoInicioShakeJugador = System.currentTimeMillis();
            }

            if (estadoActual == Combate.Estado.ESPERAR_JUGADOR
                    && estadoAnterior == Combate.Estado.TURNO_ENEMIGO) {
                mensajeActual = gestorCombate.getMensajeTurno()
                        + "\n¿Qué hará " + jugador.getNombre() + "?";
            }

            estadoAnterior = estadoActual;
        }

        // Shake decay
        long ahora = System.currentTimeMillis();
        if (tiempoInicioShakeEnemigo > 0) {
            float elapsed = ahora - tiempoInicioShakeEnemigo;
            shakeEnemigo = (elapsed < SHAKE_DURACION)
                    ? 12f * (float) Math.sin(elapsed * 0.08f) * (1 - elapsed / SHAKE_DURACION)
                    : 0f;
        }
        if (tiempoInicioShakeJugador > 0) {
            float elapsed = ahora - tiempoInicioShakeJugador;
            shakeJugador = (elapsed < SHAKE_DURACION)
                    ? 10f * (float) Math.sin(elapsed * 0.08f) * (1 - elapsed / SHAKE_DURACION)
                    : 0f;
        }

        // Flash fase 2 timeout
        if (mostrarFlashFase2 && ahora - tiempoFlashFase2 > DURACION_FLASH) {
            mostrarFlashFase2 = false;
        }

        // Ir a pantalla final
        if (estadoActual == Combate.Estado.FIN_VICTORIA) {
            if (ahora - tiempoInicioShakeEnemigo > 1500) {
                gestorEscenas.cambiarEscena(new EscenaFinal(context, gestorEscenas, true));
            }
        }
        if (estadoActual == Combate.Estado.FIN_DERROTA) {
            if (ahora - tiempoInicioShakeJugador > 1500) {
                gestorEscenas.cambiarEscena(new EscenaFinal(context, gestorEscenas, false));
            }
        }
    }

    // ─── Renderizado ─────────────────────────────────────────────────────────

    @Override
    public void renderizar(Canvas canvas) {

        if (canvas == null) return;

        w = canvas.getWidth();
        h = canvas.getHeight();
        if (!dimensionesCalculadas) {
            calcularBotones();
            escalarFondo((int) w, (int) h);
            dimensionesCalculadas = true;
        }

        if (fondoJefe != null) {
            canvas.drawBitmap(fondoJefe, 0, 0, null);
        } else {
            canvas.drawColor(Color.BLACK);
        }

        float altoSprites = h * 0.48f;
        float altoMensaje = h * 0.20f;
        float altoBotones = h * 0.32f;
        float yMensaje = altoSprites;
        float yBotones = yMensaje + altoMensaje;

        dibujarZonaSprites(canvas, altoSprites);
        dibujarCajaMensaje(canvas, yMensaje, altoMensaje);
        dibujarBotonesAtaque(canvas, yBotones, altoBotones);

        // Flash de fase 2
        if (mostrarFlashFase2) {
            long elapsed = System.currentTimeMillis() - tiempoFlashFase2;
            int alpha = (int) (200 * (1 - (float) elapsed / DURACION_FLASH));
            Paint flashPaint = new Paint();
            flashPaint.setColor(Color.argb(alpha, 255, 0, 0));
            canvas.drawRect(0, 0, w, h, flashPaint);

            String textoFase = "¡¡CIBER FRANCO ENTRA EN FASE 2!!";
            float tw = paintFase2.measureText(textoFase);
            paintFase2.setAlpha(alpha);
            canvas.drawText(textoFase, w / 2f - tw / 2f, h / 2f, paintFase2);
            paintFase2.setAlpha(255);
        }
    }

    private void dibujarZonaSprites(Canvas canvas, float altoZona) {
        // Línea de suelo: ~68% del alto de la zona de sprites
        float ysuelo = altoZona * 0.68f;

        // Sprite enemigo (izquierda) — pies en la línea de suelo
        float exEnemigo = w * 0.08f + shakeEnemigo;
        float eyEnemigo = ysuelo - spriteEnemigo.getHeight();

        if (enemigo.getFase() == Jefe.Fase.FASE_2) {
            Paint tintRojo = new Paint();
            tintRojo.setColorFilter(new android.graphics.PorterDuffColorFilter(
                    Color.argb(80, 255, 0, 0),
                    android.graphics.PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(spriteEnemigo, exEnemigo, eyEnemigo, tintRojo);
        } else {
            canvas.drawBitmap(spriteEnemigo, exEnemigo, eyEnemigo, null);
        }

        // HP bar enemigo — encima del sprite, pegada al borde superior
        float byE = eyEnemigo - 48f;
        dibujarBarraHpConNombre(canvas, exEnemigo, byE, 200,
                "CIBER FRANCO", enemigo.getVidaEnemigo(), enemigo.getVidaEnemigoMax(),
                enemigo.getPorcentajeHp(), false);

        // Indicador fase 2
        if (enemigo.getFase() == Jefe.Fase.FASE_2) {
            paintFase2.setTextSize(20);
            canvas.drawText("⚠ FASE 2", exEnemigo + 210, byE, paintFase2);
            paintFase2.setTextSize(40);
        }

        // Sprite jugador (derecha) — pies en la línea de suelo
        float exJugador = w * 0.72f + shakeJugador;
        float eyJugador = ysuelo - spriteJugador.getHeight();
        canvas.drawBitmap(spriteJugador, exJugador, eyJugador, null);

        // HP bar jugador — encima del sprite
        float byJ = eyJugador - 48f;
        dibujarBarraHpConNombre(canvas, exJugador, byJ, 200,
                jugador.getNombre(), jugador.getVida(), jugador.getVidaMax(),
                jugador.getPorcentajeHp(), true);
    }

    // Metodo auxiliar que agrupa nombre + barra + HP en un bloque limpio
    private void dibujarBarraHpConNombre(Canvas canvas, float x, float y, float ancho,
                                         String nombre, int vidaActual, int vidaMax,
                                         float porcentaje, boolean esJugador) {
        // Fondo semitransparente detrás del bloque HP
        Paint fondoHp = new Paint();
        fondoHp.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRoundRect(x - 6, y - 28, x + ancho + 6, y + 22, 6, 6, fondoHp);

        // Nombre
        paintNombrePersonaje.setColor(esJugador
                ? Color.rgb(100, 220, 255)
                : Color.rgb(255, 235, 59));
        canvas.drawText(nombre, x, y - 8, paintNombrePersonaje);


        // Texto HP pequeño
        Paint paintHpNum = new Paint();
        paintHpNum.setColor(Color.WHITE);
        paintHpNum.setTextSize(20);
        paintHpNum.setTypeface(Typeface.MONOSPACE);
        paintHpNum.setAntiAlias(false);
        canvas.drawText("HP: " + vidaActual + "/" + vidaMax, x, y + 22, paintHpNum);
    }

    private void dibujarCajaMensaje(Canvas canvas, float yMensaje, float altoMensaje) {
        // Fondo semitransparente — protagonismo al fondo y sprites
        Paint fondoCaja = new Paint();
        fondoCaja.setColor(Color.argb(200, 10, 10, 15));
        canvas.drawRect(0, yMensaje, w, yMensaje + altoMensaje, fondoCaja);

        // Línea separadora superior sutil
        Paint linea = new Paint();
        linea.setColor(Color.argb(180, 255, 235, 59));
        linea.setStrokeWidth(1.5f);
        canvas.drawLine(0, yMensaje, w, yMensaje, linea);

        // Texto grande y centrado
        paintMensaje.setTextSize(34);
        String[] lineas = mensajeActual.split("\n");
        float lineH = paintMensaje.getTextSize() + 12;
        float totalAltoTexto = lineas.length * lineH;
        float startY = yMensaje + (altoMensaje - totalAltoTexto) / 2f + paintMensaje.getTextSize();

        for (String linea2 : lineas) {
            float tw = paintMensaje.measureText(linea2);
            canvas.drawText(linea2, w / 2f - tw / 2f, startY, paintMensaje);
            startY += lineH;
        }
    }

    private void dibujarBotonesAtaque(Canvas canvas, float yBotones, float altoBotones) {
        if (botonesAtaque == null) return;

        List<Ataques> ataques = jugador.getAtaques();
        boolean puedeSeleccionar = gestorCombate.getEstado() == Combate.Estado.ESPERAR_JUGADOR;

        // Fondo general del área de botones
        Paint fondoArea = new Paint();
        fondoArea.setColor(Color.argb(210, 5, 5, 10));
        canvas.drawRect(0, yBotones, w, yBotones + altoBotones, fondoArea);

        // Línea separadora superior
        Paint lineaSep = new Paint();
        lineaSep.setColor(Color.argb(180, 255, 235, 59));
        lineaSep.setStrokeWidth(1.5f);
        canvas.drawLine(0, yBotones, w, yBotones, lineaSep);

        for (int i = 0; i < botonesAtaque.length; i++) {
            RectF btn = botonesAtaque[i];

            if (i < ataques.size()) {
                Ataques atk = ataques.get(i);

                // Fondo del botón — minimalista, solo un borde
                Paint fondoBtn = new Paint();
                fondoBtn.setColor(puedeSeleccionar
                        ? Color.argb(0, 0, 0, 0)   // transparente cuando activo
                        : Color.argb(80, 30, 30, 30)); // gris oscuro cuando inactivo
                fondoBtn.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(btn, 8, 8, fondoBtn);

                // Borde del botón
                Paint bordeBtn = new Paint();
                bordeBtn.setStyle(Paint.Style.STROKE);
                bordeBtn.setStrokeWidth(1.5f);
                bordeBtn.setColor(puedeSeleccionar
                        ? Color.argb(200, 255, 235, 59)  // amarillo activo
                        : Color.argb(80, 150, 150, 150)); // gris inactivo
                canvas.drawRoundRect(btn, 8, 8, bordeBtn);

                // Nombre del ataque — grande y centrado verticalmente
                paintTextoBtn.setColor(puedeSeleccionar ? Color.WHITE : Color.GRAY);
                paintTextoBtn.setTextSize(28);
                float tw = paintTextoBtn.measureText(atk.getNombre());
                float tx = btn.centerX() - tw / 2f;
                float ty = btn.centerY() - 14;
                canvas.drawText(atk.getNombre(), tx, ty, paintTextoBtn);

                // Potencia — pequeña, debajo, color sutil
                Paint paintPotencia = new Paint();
                paintPotencia.setTextSize(19);
                paintPotencia.setTypeface(Typeface.MONOSPACE);
                paintPotencia.setAntiAlias(false);
                paintPotencia.setColor(puedeSeleccionar
                        ? Color.argb(180, 180, 210, 255)
                        : Color.argb(80, 150, 150, 150));
                String potStr = "POT " + atk.getPotencia();
                float tw2 = paintPotencia.measureText(potStr);
                canvas.drawText(potStr, btn.centerX() - tw2 / 2f, ty + 30, paintPotencia);
            }
        }
    }

    private void calcularBotones() {
        float yBotones = h * 0.68f;
        float altoBotones = h * 0.32f;
        int n = jugador.getAtaques().size();

        // Separadores verticales entre botones (líneas, no márgenes gordos)
        float margen = 8f;
        float anchoTotal = w - margen * 2;
        float anchoBtn = anchoTotal / n;

        botonesAtaque = new RectF[n];
        for (int i = 0; i < n; i++) {
            float x = margen + i * anchoBtn;
            botonesAtaque[i] = new RectF(
                    x + 4,
                    yBotones + 10,
                    x + anchoBtn - 4,
                    yBotones + altoBotones - 10
            );
        }
    }

    // ─── Táctil ──────────────────────────────────────────────────────────────

    @Override
    public void onTouch(float x, float y) {
        if (gestorCombate.getEstado() != Combate.Estado.ESPERAR_JUGADOR) return;
        if (botonesAtaque == null) return;

        for (int i = 0; i < botonesAtaque.length; i++) {
            if (botonesAtaque[i].contains(x, y)) {
                gestorCombate.jugadorAtaca(i);
                break;
            }
        }
    }

    private void escalarFondo(int screenW, int screenH) {
        if (fondoJefe == null) return;

        // Escalar tipo "cover": la imagen cubre toda la pantalla
        // manteniendo proporciones, recortando si hace falta
        float scaleX = (float) screenW / fondoJefe.getWidth();
        float scaleY = (float) screenH / fondoJefe.getHeight();
        float scale = Math.max(scaleX, scaleY); // cover = el mayor de los dos

        int nuevoAncho = (int) (fondoJefe.getWidth() * scale);
        int nuevoAlto = (int) (fondoJefe.getHeight() * scale);

        Bitmap escalado = Bitmap.createScaledBitmap(fondoJefe, nuevoAncho, nuevoAlto, true);

        // Recortar al centro
        int offsetX = (nuevoAncho - screenW) / 2;
        int offsetY = (nuevoAlto - screenH) / 2;
        fondoJefe = Bitmap.createBitmap(escalado, offsetX, offsetY, screenW, screenH);
        escalado.recycle();
    }
}
