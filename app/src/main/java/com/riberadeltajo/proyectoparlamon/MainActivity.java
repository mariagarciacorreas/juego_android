package com.riberadeltajo.proyectoparlamon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private AnimadorPantallaInicio animadorInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        animadorInicio = new AnimadorPantallaInicio();

        ImageView logo = findViewById(R.id.imgLogo);
        Button boton = findViewById(R.id.btnPlay);
        TextView txtSlogan = findViewById(R.id.txtSlogan);
        TextView txtCreadores = findViewById(R.id.txtCreadores);

        //animaciones
        animadorInicio.animarLogo(logo);
        animadorInicio.animarBoton(boton);
        animadorInicio.animarFadeIn(txtSlogan);
        animadorInicio.animarFadeIn(txtCreadores);
    }
}