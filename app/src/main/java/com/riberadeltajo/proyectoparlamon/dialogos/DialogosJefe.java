package com.riberadeltajo.proyectoparlamon.dialogos;

import java.util.Arrays;
import java.util.List;

/**
 * Diálogos de la cutscene previa al combate con CiberFranco.
 * Se muestran en EscenaDialogoCombate usando el EscritorTexto existente.
 */
public class DialogosJefe {

    /**
     * Secuencia de pantallas de diálogo.
     * Cada List<String> es una pantalla completa (igual que dialogosLore).
     */
    public static List<List<String>> getDialogos() {
        return Arrays.asList(

                // Pantalla 1 — Ciber Franco aparece
                Arrays.asList(
                        "...",
                        "Una presencia digital llena la sala.",
                        "",
                        "Una figura pixelada, con bigote cuadrado",
                        "y uniforme de camuflaje binario,",
                        "emerge de las sombras de internet.",
                        "",
                        "Es él.",
                        "CIBER FRANCO."
                ),

                // Pantalla 2 — CiberFranco habla
                Arrays.asList(
                        "CIBER FRANCO:",
                        "\"¡Ja! ¿Así que has llegado hasta aquí?\"",
                        "",
                        "\"Impresionante... para ser un simple",
                        "ciudadano con derechos.\"",
                        "",
                        "\"La Constitución está bajo mi custodia.",
                        "Y ahí se quedará. Para siempre.\""
                ),

                // Pantalla 3 — El jugador responde / narrador
                Arrays.asList(
                        "No puedes permitirlo.",
                        "Los derechos del pueblo no son un trofeo.",
                        "",
                        "Le señalas con determinación.",
                        "",
                        "\"¡Devuelve la Constitución, Franco!",
                        "¡El pueblo te lo exige!\""
                ),

                // Pantalla 4 — CiberFranco acepta el combate
                Arrays.asList(
                        "CIBER FRANCO:",
                        "\"¿Que te la devuelva? ¡JAMÁS!\"",
                        "",
                        "\"Llevas toda la partida huyendo",
                        "de la realidad. Ahora la enfrentarás...\"",
                        "",
                        "\"¡¡CONMIGO!!\""
                ),

                // Pantalla 5 — Inicio del combate
                Arrays.asList(
                        "...",
                        "El suelo tiembla.",
                        "Las pantallas a tu alrededor",
                        "se llenan de propaganda.",
                        "",
                        "¡COMIENZA EL COMBATE!"
                )
        );
    }
}
