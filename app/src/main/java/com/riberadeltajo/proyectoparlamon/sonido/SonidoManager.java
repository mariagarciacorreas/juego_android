package com.riberadeltajo.proyectoparlamon.sonido;

import android.content.Context;
import android.media.MediaPlayer;

import com.riberadeltajo.proyectoparlamon.R;

/**
 * SonidoManager centraliza el control de la reproducción de audio del juego.
 * Implementa una arquitectura estática (Singleton global) para asegurar que la
 * música de fondo se mantenga persistente e ininterrumpida entre los cambios de escena.
 */
public class SonidoManager {

    // Instancia global para el reproductor
    // Se declara estática para que todas las escenas compartan el mismo hilo de reproducción.
    private static MediaPlayer mp;

    /**
     * Inicia de forma segura la reproducción de la pista musical base del juego.
     * Si la música ya se está reproduciendo, ignora la petición para evitar solapamientos acústicos.
     * * @param context El contexto de la aplicación o actividad necesario para acceder a los recursos raw.
     */
    public static void reproducirMusica(Context context, int resId ) {
        if (mp == null) {
            mp = MediaPlayer.create(context, resId);
            mp.setLooping(true);
            mp.start();
        }
        // si ya está creado y sonando, no hacemos nada -> mantener continuidad
    }

    /**
     * Detiene la reproducción actual de golpe y libera de forma estricta todos los
     * recursos de hardware asignados por el sistema operativo Android.
     */
    public static void detenerMusica() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
