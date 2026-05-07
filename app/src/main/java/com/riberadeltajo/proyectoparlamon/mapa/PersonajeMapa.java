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

    private final AnimacionPersonaje animacionPJ;

    private final int ANCHO_PERSONAJE = 150;

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
                recurso = R.drawable.sprite_guerrero;
                break;
            case "mago":
                recurso = R.drawable.sprite_mago;
                break;
            case "elfo":
                recurso = R.drawable.sprite_elfo;
                break;
            default:
                Log.e("PERSONAJE", "Clase desconocida: " + jugador.getClase());
                recurso = R.drawable.sprite_guerrero;
                break;
        }

        Bitmap original = BitmapFactory.decodeResource(contexto.getResources(), recurso);

        // Protección por si el drawable no existe
        if (original == null) {
            Log.e("PERSONAJE", "Bitmap null para recurso: " + recurso);
            original = BitmapFactory.decodeResource(contexto.getResources(),
                    R.drawable.sprite_elfo); // fallback seguro
        }

        animacionPJ = new AnimacionPersonaje(original, ANCHO_PERSONAJE);

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

        animacionPJ.actualizar(dx, dy);

        float nuevaX = Math.max(0, Math.min(anchoMundo - animacionPJ.getAnchoPJ(), mundoX + dx * velocidadMax));
        float nuevaY = Math.max(0, Math.min(altoMundo  - animacionPJ.getAltoPJ(),  mundoY + dy * velocidadMax));

        if (colisiones.rectTransitable(nuevaX, mundoY, animacionPJ.getAnchoPJ(), animacionPJ.getAltoPJ()))
            mundoX = nuevaX;
        if (colisiones.rectTransitable(mundoX, nuevaY, animacionPJ.getAnchoPJ(), animacionPJ.getAltoPJ()))
            mundoY = nuevaY;
    }

    //dibujar el personaje
    public void dibujar(Canvas canvas, Paint paint, Camara camara) {
        animacionPJ.dibujar(canvas, paint, camara.mundoAPantallaX(mundoX), camara.mundoAPantallaY(mundoY));
    }

    public float getMundoX() {
        return mundoX;
    }
    public float getMundoY() {
        return mundoY;
    }

    public int getAncho() {
        return animacionPJ.getAnchoPJ();
    }
    public int getAlto()  {
        return animacionPJ.getAltoPJ();
    }



}
