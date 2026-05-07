package com.riberadeltajo.proyectoparlamon.inicio;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.riberadeltajo.proyectoparlamon.R;

/**
 * La clase AnimadorPantallaInicio se encarga de gestionar las transiciones y efectos
 * visuales de los elementos de la UI nativa (Views) en la pantalla de bienvenida.
 * Utiliza el framework de animaciones de Android para lograr fluidez sin cargar el hilo principal.
 */
public class AnimadorPantallaInicio {

    public AnimadorPantallaInicio() {
    }

    /**
     * Aplica una animación de traslación al logo.
     * El logo cae desde una posición superior (-300px) hasta su posición original.
     * * @param logo La vista (ImageView) que contiene el logotipo del juego.
     */
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

    /**
     * Crea un efecto de "latido" o pulsación constante en un botón.
     * Ideal para llamar la atención del jugador sobre el botón "Jugar".
     * * @param boton La vista que recibirá el efecto de pulsación.
     */
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

    /**
     * Realiza un efecto de desvanecimiento (Fade In).
     * Nota: Actualmente configurado de 0f a 0f (invisible).
     * Para un Fade In estándar debería ser de 0.0f a 1.0f.
     * * @param v Vista sobre la que se aplicará la transparencia.
     */
    public void animarFadeIn(View v){
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(1000);
        v.startAnimation(fade);
    }

    /**
     * Carga y ejecuta una animación compleja definida en un archivo XML externo.
     * Útil para efectos largos como créditos o movimientos de marquesina.
     * * @param v Vista que ejecutará la animación definida en R.anim.creditos.
     */
    public void animarMarquesina(View v){
        //cargar animación xml
        Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.creditos);
        v.startAnimation(anim);
    }


}
