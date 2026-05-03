package com.riberadeltajo.proyectoparlamon.sonido;

import android.content.Context;
import android.media.MediaPlayer;

import com.riberadeltajo.proyectoparlamon.R;

public class SonidoManager {

    private static MediaPlayer mp;
    public static void playBase(Context context) {
        if (mp == null) {
            mp = MediaPlayer.create(context, R.raw.ep1_base);
            mp.setLooping(true);
            mp.start();
        }
        // si ya está creado y sonando, no hacemos nada
    }

    public static void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
