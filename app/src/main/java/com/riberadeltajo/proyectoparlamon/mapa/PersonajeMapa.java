package com.riberadeltajo.proyectoparlamon.mapa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.riberadeltajo.proyectoparlamon.R;
import com.riberadeltajo.proyectoparlamon.combate.Jugador;

/**
 * PersonajeMapa gestiona la lógica física, posicional y el sprite animado del
 * protagonista dentro del mapa bidimensional. Mantiene sus coordenadas absolutas
 * respecto al mundo y procesa su movimiento filtrándolo por el sistema de colisiones.
 */public class PersonajeMapa {

    //posición en coordenadas de mundo
    private float mundoX;
    private float mundoY;
    private final float velocidadMax; //factor de desplazamiento por frame

    private final Bitmap sprite;

    /**
     * Constructor: Determina la hoja de sprites (Spritesheet) adecuada según las estadísticas
     * del Jugador e inicializa su posición física inicial en el mapa.
     */
    public PersonajeMapa(Context contexto, Jugador jugador, float xInicial, float yInicial) {
        this.mundoX = xInicial;
        this.mundoY = yInicial;
        this.velocidadMax = jugador.getVelocidad() * 0.35f;

        int recurso;

        android.util.Log.d("PERSONAJE", "Clase recibida: '" + jugador.getClase() + "'");

        switch (jugador.getClase().toLowerCase()){
            case "guerrero":
                recurso = R.drawable.sprite_guerrero_ejemplo;
                break;
            case "mago":
                recurso = R.drawable.sprite_mago_ejemplo;
                break;
            case "elfo":
                recurso = R.drawable.sprite_elfo_ejemplo;
                break;
            default:
                Log.e("PERSONAJE", "Clase desconocida: " + jugador.getClase());
                recurso = R.drawable.sprite_guerrero_ejemplo;
                break;
        }

        Bitmap original = BitmapFactory.decodeResource(contexto.getResources(), recurso);

        // Protección por si el drawable no existe
        if (original == null) {
            Log.e("PERSONAJE", "Bitmap null para recurso: " + recurso);
            original = BitmapFactory.decodeResource(contexto.getResources(),
                    R.drawable.sprite_elfo_ejemplo); // fallback seguro
        }


        int ancho = 120;
        int alto  = (int)(original.getHeight() * (ancho / (float) original.getWidth()));
        sprite = Bitmap.createScaledBitmap(original, ancho, alto, false);
        original.recycle();
    }

    /**
     * Actualiza la posición física del personaje aplicando las fuerzas de entrada (dx, dy),
     * limitando los márgenes del mundo (Clamping) y resolviendo colisiones por ejes separados.
     * * @param dx Dirección horizontal del input (Rango entre -1.0 y 1.0)
     * @param dy Dirección vertical del input (Rango entre -1.0 y 1.0)
     * @param anchoMundo Límite horizontal máximo del mapa actual en píxeles
     * @param altoMundo Límite vertical máximo del mapa actual en píxeles
     * @param colisiones Instancia de la matriz de baldosas transitables/sólidas
     */
    public void actualizar(float dx, float dy, float anchoMundo, float altoMundo, MapaColisiones colisiones) {

        float nuevaX = mundoX + dx * velocidadMax;
        float nuevaY = mundoY + dy * velocidadMax;
//        mundoX += dx * velocidadMax;
//        mundoY += dy * velocidadMax;

        //limitar los bordes del mundo
        nuevaX = Math.max(0, Math.min(anchoMundo - sprite.getWidth(),  nuevaX));
        nuevaY = Math.max(0, Math.min(altoMundo  - sprite.getHeight(), nuevaY));

        //comrpobar colisones
        if (colisiones.rectTransitable(nuevaX, mundoY, sprite.getWidth(), sprite.getHeight())) {
            mundoX = nuevaX;
        }
        if (colisiones.rectTransitable(mundoX, nuevaY, sprite.getWidth(), sprite.getHeight())) {
            mundoY = nuevaY;
        }
    }

    //dibujar el personaje
    public void dibujar(Canvas canvas, Paint paint, Camara camara) {
        float px = camara.mundoAPantallaX(mundoX);
        float py = camara.mundoAPantallaY(mundoY);
        canvas.drawBitmap(sprite, px, py, paint);
    }

    public float getMundoX() {
        return mundoX;
    }
    public float getMundoY() {
        return mundoY;
    }

    public int getAncho() {
        return sprite.getWidth();
    }
    public int getAlto()  {
        return sprite.getHeight();
    }



}
