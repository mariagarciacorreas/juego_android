package com.riberadeltajo.proyectoparlamon.dialogos;

import java.util.Arrays;
import java.util.List;

public class GestorDialogos {

    public GestorDialogos() {
    }

    private final List<List<String>> dialogosLore = Arrays.asList(
            //Pantalla 1
            Arrays.asList(
                    "En un reino no tan lejano",
                    "donde los memes gobiernan más que las leyes,",
                    "una sombra digital se extiende sobre la nación…",
                    "",
                    "La Gran Carta, símbolo de los derechos del",
                    "pueblo, ha sido robada por el temible…",
                    "Ciber Frank, señor del ciberfascismo.",
                    "",
                    "Con la libertad en peligro,",
                    "solo un héroe puede restaurar el equilibrio.",
                    "Ese héroe… podrías ser tú."
            ),
            //Pantalla 2
            Arrays.asList(
                    "Bienvenido a Ciudad Memópolis,",
                    "donde cada esquina es un debate",
                    "y cada vecino tiene una opinión.",
                    "",
                    "Antes de comenzar tu misión,",
                    "debes elegir a tu campeón.",
                    "Cada uno posee habilidades únicas…",
                    "y un ego considerable.",
                    "",
                    "Con la libertad en peligro,",
                    "solo un héroe puede restaurar el equilibrio.",
                    "Ese héroe… podrías ser tú."
            )
    );


    public List<List<String>> getDialogosLore() {
        return dialogosLore;
    }
}
