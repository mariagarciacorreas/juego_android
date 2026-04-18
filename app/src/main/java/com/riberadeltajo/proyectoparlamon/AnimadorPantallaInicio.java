package com.riberadeltajo.proyectoparlamon;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimadorPantallaInicio {

    public void animarLogo(View logo){
        //aparece desde arriba con rebote suave
        TranslateAnimation anim = new TranslateAnimation(
                0, 0,
                -300, 0
        );
        anim.setDuration(1000);
        anim.setFillAfter(true);
        logo.startAnimation(anim);
    }

    public void animarBoton(View boton){
        //efecto latido
        ScaleAnimation scale = new ScaleAnimation(
                1f, 1.08f,
                1f, 1.08f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(600);
        scale.setRepeatCount(Animation.INFINITE);
        scale.setRepeatMode(Animation.REVERSE);
        boton.startAnimation(scale);
    }

    public void animarFadeIn(View v){
        AlphaAnimation fade = new AlphaAnimation(0f, 0f);
        fade.setDuration(1000);
        v.startAnimation(fade);
    }


}
