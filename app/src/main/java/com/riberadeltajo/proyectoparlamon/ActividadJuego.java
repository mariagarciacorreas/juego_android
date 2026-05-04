

package com.riberadeltajo.proyectoparlamon;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.riberadeltajo.proyectoparlamon.motor.Juego;

public class ActividadJuego extends AppCompatActivity {

    private Juego juego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Bloquear horizontal siempre
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // Cargar motor del juego
        juego = new Juego(this);
        setContentView(juego);

        // Pantalla completa antes de inflar cualquier vista
        solicitarPantallaCompleta();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-aplicar al volver de segundo plano (barra de notificaciones, llamada, etc.)
        solicitarPantallaCompleta();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Re-aplicar cuando el foco vuelve tras diálogos del sistema
        if (hasFocus) {
            solicitarPantallaCompleta();
        }
    }

    /**
     * Fuerza pantalla completa real en todas las versiones de Android:
     *  - API 30+ (Android 11+): WindowInsetsController moderno
     *  - API 19–29:             flags de visibilidad immersive sticky
     *
     * También extiende el layout al área del notch/recorte de cámara.
     */
    private void solicitarPantallaCompleta() {
        Window window = getWindow();

        //mantener pantalla activa
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // ✅ CAMBIO: getInsetsController() puede ser null si la ventana aún no está
            // adjunta al decorado (ocurre cuando se llama desde onCreate antes de setContentView).
            // Lo envolvemos en un post() para ejecutarlo cuando la ventana ya esté lista.
            window.getDecorView().post(() -> {
                WindowInsetsController controller = window.getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars()
                            | WindowInsets.Type.navigationBars()
                            | WindowInsets.Type.displayCutout());
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            });
        } else {
            View decorView = window.getDecorView();
            //noinspection deprecation
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            //noinspection deprecation
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    solicitarPantallaCompleta();
                }
            });
        }
    }
}