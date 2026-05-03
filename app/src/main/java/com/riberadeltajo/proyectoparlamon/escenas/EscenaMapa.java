package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.combate.Jugador;
import com.riberadeltajo.proyectoparlamon.hud.GestorHUD;
import com.riberadeltajo.proyectoparlamon.mapa.Camara;
import com.riberadeltajo.proyectoparlamon.mapa.GestorControles;
import com.riberadeltajo.proyectoparlamon.mapa.MapaColisiones;
import com.riberadeltajo.proyectoparlamon.mapa.PersonajeMapa;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;
import com.riberadeltajo.proyectoparlamon.sonido.SonidoManager;

public class EscenaMapa implements Escena{

    private static final boolean DEBUG_ZONAS = true; // cambiar a false para release


    //dependencias
    private Context context;
    private GestorEscenas gestorEscenas;
    private final Jugador jugador;

    //zoom fijo
    private static final float ZOOM_FACTOR = 2.0f; //mapa al triple de su tamaño original

    //zona de encuentro con el final boss (coordenadas de mundo)
    private static final float ZONA_MUNDO_X = 1400f; //pixeles en el mapa escalado
    private static final float ZONA_MUNDO_Y = 480f;
    private static final float RADIO_ZONA = 90f;

    //subsistema
    private Bitmap mapaEscalado; //bitmap con el zoom de la vista aplicado
    private Camara camara;
    private PersonajeMapa personaje;
    private GestorControles gestorControles;
    private GestorHUD gestorHUD;

    //estado
    private float anchoPantalla = 0f;
    private float altoPantalla = 0f;
    private boolean inicializado = false;

    //paints
    private final Paint paintSrpite = new Paint();
    private final Paint paintMapa = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Paint paintHudTexto;

    //paint para zona de encuentro - marcador visual
    private final Paint paintZonaRelleno;
    private final Paint paintZonaBorde;

    //zona congreso - batalla con final boss - esquina superior derecha del mapa
    private static final float CONGRESO_MUNDO_X = 7550f;
    private static final float CONGRESO_MUNDO_Y = 1440f;
    private static final float RADIO_CONGRESO   = 150f;

    // Estado del overlay del congreso
    private boolean mostrarOverlayCongreso = false;

    //cartel advertencia y overlay
    private final Paint paintCartel;
    private final Paint paintCartelTexto;
    private final Paint paintOverlay;
    private final Paint paintOverlayTexto;
    private final Paint paintBotonEntrar;
    private RectF botonEntrarRect;

    private final Paint paintBotonTexto;

    //mapa colisiones (mapa transitable)
    private MapaColisiones mapaColisiones;

    //sonido
    private final MediaPlayer mpCongreso;
    private final MediaPlayer mpBase;


    //constructor
    public EscenaMapa(Context context, GestorEscenas gestorEscenas, Jugador jugador) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;
        this.jugador = jugador;

        SonidoManager.stop();

        paintHudTexto = new Paint();
        paintHudTexto.setColor(Color.WHITE);
        paintHudTexto.setTextSize(28);
        paintHudTexto.setTypeface(Typeface.MONOSPACE);
        paintHudTexto.setAntiAlias(false);

        paintZonaRelleno = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintZonaRelleno.setColor(Color.argb(80, 255, 60, 60));
        paintZonaRelleno.setStyle(Paint.Style.FILL);

        paintZonaBorde = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintZonaBorde.setColor(Color.rgb(255, 60, 60));
        paintZonaBorde.setStyle(Paint.Style.STROKE);
        paintZonaBorde.setStrokeWidth(2f);

        //zona congreso
        paintCartel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCartel.setColor(Color.rgb(180, 30, 30));
        paintCartel.setStyle(Paint.Style.FILL);

        paintCartelTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCartelTexto.setColor(Color.WHITE);
        paintCartelTexto.setTextSize(28f);
        paintCartelTexto.setTypeface(Typeface.MONOSPACE);
        paintCartelTexto.setTextAlign(Paint.Align.CENTER);

        paintOverlay = new Paint();
        paintOverlay.setColor(Color.argb(200, 0, 0, 0));

        paintOverlayTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOverlayTexto.setColor(Color.rgb(255, 235, 59));
        paintOverlayTexto.setTextSize(48f);
        paintOverlayTexto.setTypeface(Typeface.MONOSPACE);
        paintOverlayTexto.setTextAlign(Paint.Align.CENTER);

        paintBotonEntrar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBotonEntrar.setColor(Color.rgb(180, 30, 30));
        paintBotonEntrar.setStyle(Paint.Style.FILL);

        paintBotonTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBotonTexto.setColor(Color.WHITE);
        paintBotonTexto.setTextSize(42f);
        paintBotonTexto.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        paintBotonTexto.setTextAlign(Paint.Align.CENTER);

        //inicializar sonido
        mpCongreso = MediaPlayer.create(context, R.raw.ep2_congreso);
        mpCongreso.setOnCompletionListener(mp -> mp.release());
        mpBase = MediaPlayer.create(context, R.raw.ep2_base);
        mpBase.start();
    }

    @Override
    public void actualizar() {
        if(!inicializado) return;

        gestorHUD.actualizar();

        if(gestorHUD.isPausado()) return;

        //mover al personaje en el mundo (coordenadas mundo)
        personaje.actualizar(gestorControles.getDx(), gestorControles.getDy(),
                            mapaEscalado.getWidth(), mapaEscalado.getHeight(), mapaColisiones);

        //actualizar cámara con la posición del personaje
        camara.actualizar(personaje.getMundoX(), personaje.getMundoY());

        comprobarZonaEncuentro();
    }

    @Override
    public void renderizar(Canvas canvas) {
        if (canvas == null) return;

        inicializacionDiferida(canvas);

        canvas.drawColor(Color.BLACK);

        //mapa desplazado
        canvas.drawBitmap(mapaEscalado, -camara.getCamX(), -camara.getCamY(), paintMapa);


        //zona de encuentro
        if (DEBUG_ZONAS) {
            float congresoX = camara.mundoAPantallaX(CONGRESO_MUNDO_X);
            float congresoY = camara.mundoAPantallaY(CONGRESO_MUNDO_Y);
            canvas.drawCircle(congresoX, congresoY, RADIO_CONGRESO, paintZonaRelleno);
            canvas.drawCircle(congresoX, congresoY, RADIO_CONGRESO, paintZonaBorde);
        }

        // Cartel de advertencia en el congreso (coordenadas pantalla)
        float cartelX = camara.mundoAPantallaX(CONGRESO_MUNDO_X);
        float cartelY = camara.mundoAPantallaY(CONGRESO_MUNDO_Y);

        // Solo dibujarlo si está en pantalla
        if (cartelX > -200 && cartelX < anchoPantalla + 200
                && cartelY > -100 && cartelY < altoPantalla + 100) {
            dibujarCartel(canvas, cartelX, cartelY);
        }

        //personaje
        personaje.dibujar(canvas, paintSrpite, camara);

        // DEBUG — coordenadas del personaje en el mundo
        Paint paintDebug = new Paint();
        paintDebug.setColor(Color.rgb(0, 255, 0));
        paintDebug.setTextSize(28f);
        paintDebug.setTypeface(Typeface.MONOSPACE);
        paintDebug.setAntiAlias(false);
        canvas.drawText( "X: " + (int)personaje.getMundoX() + "  Y: " + (int)personaje.getMundoY(), 50, 100, paintDebug);
        //DEBUG - vector joystick
        canvas.drawText("dx=" + gestorControles.getDx(),50, 140, paintDebug);
        canvas.drawText("dy=" + gestorControles.getDy(), 50, 180, paintDebug);



        //texto
        canvas.drawText("⚠ Encuentra a Ciber Franco", 20, 50, paintHudTexto);

        // Overlay congreso
        if (mostrarOverlayCongreso) {
            dibujarOverlayCongreso(canvas);
        }

        //controles táctiles - coordenadas de pantalla fijas
        if(!gestorHUD.isPausado()){
            gestorControles.dibujar(canvas);
        }

        //botones y overlays - encima de todo
        gestorHUD.dibujar(canvas);

    }

    @Override
    public void onTouch(float x, float y) {

    }


    //gestión del multitouch
    @Override
    public void onTouchEvent(MotionEvent event) {
        if (!inicializado) return;

        int accion = event.getActionMasked();
        if (accion == MotionEvent.ACTION_DOWN || accion == MotionEvent.ACTION_POINTER_DOWN) {
//            int idx = event.getActionIndex();
//            float tx = event.getX(idx);
//            float ty = event.getY(idx);


            // Si el overlay del congreso está visible, solo procesamos su botón
            for (int i = 0; i < event.getPointerCount(); i++) {
                float tx = event.getX(i);
                float ty = event.getY(i);
                Log.d("BOTON", "TOCADO en x=" + tx + " y=" + ty);

                if (mostrarOverlayCongreso && botonEntrarRect != null && botonEntrarRect.contains(tx, ty)) {
                    Log.d("BOTON", "CLICK ENTRAR DETECTADO");
                    mostrarOverlayCongreso = false;
                    gestorControles.resetearControles();
                    gestorEscenas.cambiarEscena(new EscenaDialogoCombate(context, gestorEscenas,
                            jugador.getNombre(), jugador.getClase()));
                    mpBase.stop();
                    return;
                }
//            }

//                float bw = 300f, bh = 90f;
//                float bx = anchoPantalla / 2f - bw / 2f;
//                float by = altoPantalla / 2f + 120f;
//
//                if (tx >= bx && tx <= bx + bw && ty >= by && ty <= by + bh) {
//                    Log.d("BOTON", "CLICK ENTRAR DETECTADO");
//                    mostrarOverlayCongreso = false;
//                    gestorControles.resetearControles();
//                    gestorEscenas.cambiarEscena(
//                            new EscenaDialogoCombate(context, gestorEscenas,
//                                    jugador.getNombre(), jugador.getClase())
//                    );
//                }
//                return; //bloquear cualquier otro toque mientras el overlay está abierto
//            }

                if (gestorHUD.onTouch(tx, ty)) return;
            }
        }

        if (!gestorHUD.isPausado()) {
            gestorControles.procesarEvento(event);
        }
    }



    //función para controlar la inicialización diferida en caso necesario
    private void inicializacionDiferida(Canvas canvas){
        if (inicializado) return;

        anchoPantalla = canvas.getWidth();
        altoPantalla  = canvas.getHeight();

        // Cargar y escalar el mapa
        Bitmap mapaOriginal = BitmapFactory.decodeResource(context.getResources(), R.drawable.memopolis);

        mapaColisiones = new MapaColisiones(context, ZOOM_FACTOR);

        int anchoEscalado = Math.round(mapaOriginal.getWidth()  * ZOOM_FACTOR);
        int altoEscalado  = Math.round(mapaOriginal.getHeight() * ZOOM_FACTOR);
        mapaEscalado = Bitmap.createScaledBitmap(mapaOriginal, anchoEscalado, altoEscalado, true);
        mapaOriginal.recycle();

        // Cámara — conoce las dimensiones de pantalla y del mundo
        camara = new Camara(anchoPantalla, altoPantalla, mapaEscalado.getWidth(), mapaEscalado.getHeight());

        // Posición inicial del personaje
        float inicioX = 1883;
        float inicioY = 5272;
        personaje = new PersonajeMapa(context, jugador, inicioX, inicioY);

        //centra la camara sobre el personaje
        camara.centrarEn(inicioX, inicioY);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        gestorControles = new GestorControles(context, anchoPantalla, altoPantalla, dm.density);
        gestorHUD = new GestorHUD(anchoPantalla, altoPantalla, dm.density, gestorControles);

        inicializado = true;
    }

    private void comprobarZonaEncuentro(){
        //zona Ciber Franco
        float dx = personaje.getMundoX() - ZONA_MUNDO_X;
        float dy = personaje.getMundoY() - ZONA_MUNDO_Y;
        if (Math.sqrt(dx * dx + dy * dy) < RADIO_ZONA) {
            gestorControles.resetearControles();
            gestorEscenas.cambiarEscena(
                    new EscenaDialogoCombate(context, gestorEscenas,
                            jugador.getNombre(), jugador.getClase())
            );
        }

        //zona congreso
        if (!mostrarOverlayCongreso) {
            float dcx = personaje.getMundoX() - CONGRESO_MUNDO_X;
            float dcy = personaje.getMundoY() - CONGRESO_MUNDO_Y;
            if (Math.sqrt(dcx * dcx + dcy * dcy) < RADIO_CONGRESO) {

                //reproducir efecto sonido
                if (mpCongreso != null){
                    mpCongreso.start();
                }

                mostrarOverlayCongreso = true;
                gestorControles.resetearControles();
            }
        }
    }

    private void dibujarCartel(Canvas canvas, float px, float py) {
        float w = 160f;
        float h = 80f;
        float bx = px - w / 2f, by = py - h;

        // Fondo rojo del cartel
        RectF rect = new RectF(bx, by, bx + w, by + h);
        canvas.drawRoundRect(rect, 8, 8, paintCartel);

        // Borde amarillo
        Paint borde = new Paint(Paint.ANTI_ALIAS_FLAG);
        borde.setColor(Color.rgb(255, 235, 59));
        borde.setStyle(Paint.Style.STROKE);
        borde.setStrokeWidth(3f);
        canvas.drawRoundRect(rect, 8, 8, borde);

        // Texto
        canvas.drawText("⚠ PELIGRO", px, by + 30, paintCartelTexto);
        canvas.drawText("DICTADOR", px, by + 58, paintCartelTexto);
    }

    private void dibujarOverlayCongreso(Canvas canvas) {
        float w = anchoPantalla, h = altoPantalla;

        // Fondo oscuro
        canvas.drawRect(0, 0, w, h, paintOverlay);

        // Título
        canvas.drawText("⚠ ZONA PELIGROSA", w / 2f, h / 2f - 120, paintOverlayTexto);
        canvas.drawText("El Congreso", w / 2f, h / 2f - 60, paintOverlayTexto);

        paintOverlayTexto.setTextSize(32f);
        paintOverlayTexto.setColor(Color.WHITE);
        canvas.drawText("Ciber Franco te espera dentro.", w / 2f, h / 2f, paintOverlayTexto);
        canvas.drawText("No habrá vuelta atrás.", w / 2f, h / 2f + 45, paintOverlayTexto);
        // restaurar
        paintOverlayTexto.setTextSize(48f);
        paintOverlayTexto.setColor(Color.rgb(255, 235, 59));

        // Botón entrar
        float bw = 300f, bh = 90f;
        float bx = w / 2f - bw / 2f, by = h / 2f + 120f;

        botonEntrarRect = new RectF(bx, by, bx + bw, by + bh);

        RectF boton = new RectF(bx, by, bx + bw, by + bh);
        canvas.drawRoundRect(boton, 16, 16, paintBotonEntrar);
        canvas.drawText("ENTRAR", w / 2f, by + 60, paintBotonTexto);
    }
}



