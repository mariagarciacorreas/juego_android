package com.riberadeltajo.proyectoparlamon.mapa;

/*
┌─────────────────────────────────────┐
│           MUNDO (mapa)              │
│   ┌──────────────────────────┐      │
│   │      30%  PANTALLA       │      │
│   │    ┌────────────────┐    │      │
│   │    │   zona         │    │      │
│   │    │   muerta       │    │      │
│   │    └────────────────┘    │      │
│   │                          │      │
│   └──────────────────────────┘      │
└─────────────────────────────────────┘
 */
public class Camara {

    //situación inicial de la camara (esquina superior izqueirda del mundo visible
    private float camX = 0f;
    private float camY = 0f;

    //target hacia el que moverse
    private float targetX = 0f;
    private float targetY = 0f;

    //dimensiones pantalla
    private final float anchoPantalla;
    private final float altoPantalla;

    //dimensiones mundo mapa escalado
    private final float anchoMundo;
    private final float altoMundo;

    //margen de la zona muerta dentro de la que se puede mover el perosnaje
    private final float margenH; //horizontal
    private final float margenV; //vertical

    //factor de suavizado de movimiento de camara (interpolacion lineal)
    // 0.0 no se mueve,   1.0 instantáneo
    private static final float LERP = 0.08f;

    public Camara(float anchoPantalla, float altoPantalla, float anchoMundo, float altoMundo) {
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        this.anchoMundo = anchoMundo;
        this.altoMundo = altoMundo;

        this.margenV = altoPantalla * 0.30f;
        this.margenH = anchoPantalla * 0.30f;
    }

    //actualizar la cámara en cada frame según la posición del personaje
    public void actualizar(float personajeX, float personajeY){
        //posición del personaje relativa a la pantalla
        float px = personajeX - camX;
        float py = personajeY - camY;

        //zona muerta
        if (px < margenH) targetX = personajeX - margenH;
        if (px > anchoPantalla - margenH) targetX = personajeX - (anchoPantalla - margenH);
        if (py < margenV) targetY = personajeY - margenV;
        if (py > altoPantalla  - margenV) targetY = personajeY - (altoPantalla  - margenV);

        //limitar target a los bordes del mundo para que la camara no sagla del mapa
        targetX = clamp(targetX, 0, anchoMundo - anchoPantalla);
        targetY = clamp(targetY, 0, altoMundo - altoPantalla);

        //lerp hacia el target
        camX += (targetX - camX) * LERP;
        camY += (targetY - camY) * LERP;
    }

    //convertir una coordenada X del mundo a cordenada X de pantalla
    //necesario al dibujar pj o entidades en el mapa
    public float mundoAPantallaX(float mundoX){
        return mundoX - camX;
    }

    //convertir coordenada Y del mundo en coordenada Y de pantalla
    public float mundoAPantallaY(float mundoY){
        return mundoY - camY;
    }

    //convertir una coordenada X de pantalla a coordenada X del mundo
    //necesario para convertir toques de pantalla en posición en el mapa
    public float pantallaAMundoX(float mundoX){
        return mundoX + camX;
    }

    //convertir una coordenada Y de pantalla a coordenada Y del mundo
    public float pantallaAMundoY(float mundoY){
        return mundoY + camY;
    }

    //centrar posición de camara en posición concreta
    public void centrarEn(float mundoX, float mundoY){
        camX = mundoX - anchoPantalla / 2f;
        camY = mundoY - altoPantalla  / 2f;

        // Limitar a los bordes del mundo
        camX = Math.max(0, Math.min(anchoMundo - anchoPantalla, camX));
        camY = Math.max(0, Math.min(altoMundo  - altoPantalla,  camY));

        // Sincronizar el target para que no haya lerp inicial
        targetX = camX;
        targetY = camY;
    }

    private static float clamp(float valor, float min, float max) {
        return Math.max(min, Math.min(max, valor));
    }

    public float getCamX() {
        return camX;
    }

    public float getCamY() {
        return camY;
    }




}
