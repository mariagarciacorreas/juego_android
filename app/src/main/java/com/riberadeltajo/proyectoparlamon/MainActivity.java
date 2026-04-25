package com.riberadeltajo.proyectoparlamon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.riberadeltajo.proyectoparlamon.inicio.AnimadorPantallaInicio;
import com.riberadeltajo.proyectoparlamon.motor.Juego;

public class MainActivity extends AppCompatActivity {

    private AnimadorPantallaInicio animadorInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //esconder botones de menú android
        hideSystemUI();

        animadorInicio = new AnimadorPantallaInicio();

        ImageView logo = findViewById(R.id.imgLogo);
        Button boton = findViewById(R.id.btnPlay);
        TextView txtSlogan = findViewById(R.id.txtSlogan);
        TextView txtCreadores = findViewById(R.id.txtCreadores);

        //animaciones
        animadorInicio.animarLogo(logo);
        animadorInicio.animarBoton(boton);
        animadorInicio.animarFadeIn(txtSlogan);
        animadorInicio.animarMarquesina(txtCreadores);

        //acción del botón
        boton.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ActividadJuego.class);
            startActivity(i);
            //setContentView(new Juego(this));
        });

    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        View v = getWindow().getDecorView();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            //A partir de kitkat
            v.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            //cuando se presiona volumen, por ej, se cambia la visibilidad, hay que volver
            //a ocultar
            v.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    hideSystemUI();
                }
            });
        }
    }
}

