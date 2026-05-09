package com.riberadeltajo.proyectoparlamon.sonido;

import android.content.Context;
import android.media.MediaPlayer;

import com.riberadeltajo.proyectoparlamon.R;


public class SonidoManager {

    // Instancia global para el reproductor
    // Se declara estática para que todas las escenas compartan el mismo hilo de reproducción.
    private static MediaPlayer mp;


    public static void reproducirMusica(Context context, int resId ) {
        if (mp == null) {
            mp = MediaPlayer.create(context, resId);
            mp.setLooping(true);
            mp.start();
        }
        // si ya está creado y sonando, no hacemos nada -> mantener continuidad
    }

    public static void detenerMusica() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
