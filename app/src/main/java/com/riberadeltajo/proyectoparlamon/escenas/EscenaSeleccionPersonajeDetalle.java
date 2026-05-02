package com.riberadeltajo.proyectoparlamon.escenas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.combate.Jugador;
import com.riberadeltajo.proyectoparlamon.motor.GestorEscenas;

public class EscenaSeleccionPersonajeDetalle implements Escena{

    private Context context;
    private GestorEscenas gestorEscenas;

    private String nombre;
    private String clase;

    private Paint paintTexto;
    private Paint paintBoton;
    private Paint paintTextoBtn;

    //animación del botón (efecto latido)
    private float escalaBoton = 1f;
    private float velocidadLatido = 0.005f;
    private boolean creciendo = true;

    //tamaño de pantalla
    float w;
    float h;

    private Bitmap sprite;
    private Bitmap spriteBase;

    public EscenaSeleccionPersonajeDetalle(Context context, GestorEscenas gestorEscenas, String nombre, String clase) {
        this.context = context;
        this.gestorEscenas = gestorEscenas;
        this.nombre = nombre;
        this.clase = clase;

        paintTexto = new Paint();
        paintTexto.setColor(Color.rgb(255, 235, 59)); // FFEB3B
        paintTexto.setTextSize(50);
        paintTexto.setTypeface(Typeface.MONOSPACE);
        paintTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paintTexto.setAntiAlias(false);

        paintTextoBtn = new Paint();
        paintTextoBtn.setColor(Color.DKGRAY);
        paintTextoBtn.setTextSize(50);
        paintTextoBtn.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        paintTextoBtn.setAntiAlias(false);

        paintBoton = new Paint();
        paintBoton.setColor(Color.WHITE);
        paintBoton.setStyle(Paint.Style.FILL);

        cargarSpriteBase();
    }

    private void cargarSpriteBase(){
        if (clase.equalsIgnoreCase("Guerrero")) {
            spriteBase = BitmapFactory.decodeResource(context.getResources(), R.drawable.guerrero);
        } else if (clase.equalsIgnoreCase("Mago")) {
            spriteBase = BitmapFactory.decodeResource(context.getResources(), R.drawable.mago);
        } else if (clase.equalsIgnoreCase("Elfo")) {
            spriteBase = BitmapFactory.decodeResource(context.getResources(), R.drawable.elfo);
        }
    }

    @Override
    public void actualizar() {

        //animación tipo latido para el botón (1.00 → 1.08 → 1.00)
        if (creciendo) {
            escalaBoton += velocidadLatido;
            if (escalaBoton >= 1.08f) {
                creciendo = false;
            }
        } else {
            escalaBoton -= velocidadLatido;
            if (escalaBoton <= 1f) {
                creciendo = true;
            }
        }
    }

    @Override
    public void renderizar(Canvas canvas) {

        canvas.drawColor(Color.DKGRAY);

        w = canvas.getWidth();
        h = canvas.getHeight();

        //escalar sprite del personaje al 40% del tamaño de la pantalla
        if (sprite == null && spriteBase != null) {
            int nuevoAncho = (int)(w * 0.2f); // 40% del ancho de pantalla
            float ratio = (float)spriteBase.getHeight() / spriteBase.getWidth();
            int nuevoAlto = (int)(nuevoAncho * ratio);

            sprite = Bitmap.createScaledBitmap(spriteBase, nuevoAncho, nuevoAlto, false);
        }

        //nombre y clase
        canvas.drawText(nombre, w/2f - 150, 150, paintTexto);
        canvas.drawText("Clase: " + clase, w/2f - 150, 230, paintTexto);

        //sprite del personaje centrado
        if (sprite != null) {
            float spriteX = w/2f - sprite.getWidth()/2f;
            float spriteY = h/2f - sprite.getHeight()/2f;
            canvas.drawBitmap(sprite, spriteX, spriteY, null);
        }

        //botón comenzar
        float bw = 400;
        float bh = 120;

        float bx = w/2f - bw/2f;
        float by = h - 200;
        //centro del botón (punto de escala)
        float cx = bx + bw/2f;
        float cy = by + bh/2f;
        //guardar estado del canvas
        canvas.save();
        //aplicar escala al latido
        canvas.scale(escalaBoton, escalaBoton, cx, cy);
        //dibujar rectángulo botón escalado
        canvas.drawRect(bx, by, bx + bw, by + bh, paintBoton);
        //centrar texto dentro del botón automáticamente
        String texto = "Comenzar";
        float textoAncho = paintTextoBtn.measureText(texto);
        float textoX = cx - textoAncho / 2f;
        float textoY = cy + 20;
        canvas.drawText(texto, textoX, textoY, paintTextoBtn);
        //restaurar canvas
        canvas.restore();

    }

    @Override
    public void onTouch(float x, float y) {

        float bw = 400;
        float bh = 120;
        float bx = w/2f - bw/2f;
        float by = h - 200;

        if (x >= bx && x <= bx + bw && y >= by && y <= by + bh) {
            Jugador jugador = new Jugador(nombre, clase);
            gestorEscenas.cambiarEscena(
                    new EscenaMapa(context, gestorEscenas, jugador)
            );
        }
    }
}
