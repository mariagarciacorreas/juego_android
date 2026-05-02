package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.combate.Jugador;
import com.riberadeltajo.proyectoparlamon.hud.GestorHUD;
import com.riberadeltajo.proyectoparlamon.mapa.Camara;
import com.riberadeltajo.proyectoparlamon.mapa.GestorControles;
import com.riberadeltajo.proyectoparlamon.mapa.PersonajeMapa;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

public class EscenaMapa implements Escena{

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



    //private String nombre;
    //private String clase;


    //constructor
    public EscenaMapa(Context context, GestorEscenas gestorEscenas, Jugador jugador) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;
        this.jugador = jugador;

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
    }

    @Override
    public void actualizar() {
        if(!inicializado) return;

        gestorHUD.actualizar();

        if(gestorHUD.isPausado()) return;

        //mover al personaje en el mundo (coordenadas mundo)
        personaje.actualizar(gestorControles.getDx(), gestorControles.getDy(),
                            mapaEscalado.getWidth(), mapaEscalado.getHeight());

        //actualizar cámara con la posición del personaje
        camara.actualizar(personaje.getMundoX(), personaje.getMundoY());

        comprobarZonaEncuentro();
    }

    @Override
    public void renderizar(Canvas canvas) {
        inicializacionDiferida(canvas);

        canvas.drawColor(Color.BLACK);

        //mapa desplazado
        canvas.drawBitmap(mapaEscalado, -camara.getCamX(), -camara.getCamY(), paintMapa);

        //zona de encuentro
        float zonaX = camara.mundoAPantallaX(ZONA_MUNDO_X);
        float zonaY = camara.mundoAPantallaY(ZONA_MUNDO_Y);
        canvas.drawCircle(zonaX, zonaY, RADIO_ZONA, paintZonaRelleno);
        canvas.drawCircle(zonaX, zonaY, RADIO_ZONA, paintZonaBorde);

        //personaje
        personaje.dibujar(canvas, paintSrpite, camara);

        //texto
        canvas.drawText("⚠ Encuentra a Ciber Franco", 20, 50, paintHudTexto);

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
            int idx = event.getActionIndex();
            if (gestorHUD.onTouch(event.getX(idx), event.getY(idx))) return;
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

        int anchoEscalado = Math.round(mapaOriginal.getWidth()  * ZOOM_FACTOR);
        int altoEscalado  = Math.round(mapaOriginal.getHeight() * ZOOM_FACTOR);
        mapaEscalado = Bitmap.createScaledBitmap(mapaOriginal, anchoEscalado, altoEscalado, true);
        mapaOriginal.recycle();

        // Cámara — conoce las dimensiones de pantalla y del mundo
        camara = new Camara(anchoPantalla, altoPantalla, mapaEscalado.getWidth(), mapaEscalado.getHeight());

        // Personaje — arranca en el centro del mapa (ajustar a posición inicial deseada)
        float inicioX = mapaEscalado.getWidth() * 0.35f;
        float inicioY = mapaEscalado.getHeight() * 0.55f;
        personaje = new PersonajeMapa(context, jugador, inicioX, inicioY);

        //centra la camara sobre el personaje
        camara.centrarEn(inicioX, inicioY);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        gestorControles = new GestorControles(context, anchoPantalla, altoPantalla, dm.density);
        gestorHUD = new GestorHUD(anchoPantalla, altoPantalla, dm.density, gestorControles);

        inicializado = true;
    }

    private void comprobarZonaEncuentro(){
        float dx = personaje.getMundoX() - ZONA_MUNDO_X;
        float dy = personaje.getMundoY() - ZONA_MUNDO_Y;
        if (Math.sqrt(dx * dx + dy * dy) < RADIO_ZONA) {
            gestorControles.resetearControles();
            gestorEscenas.cambiarEscena(
                    new EscenaDialogoCombate(context, gestorEscenas,
                            jugador.getNombre(), jugador.getClase())
            );
        }
    }
}



