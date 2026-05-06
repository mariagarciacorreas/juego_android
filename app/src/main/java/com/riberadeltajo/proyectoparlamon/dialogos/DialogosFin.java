package com.riberadeltajo.proyectoparlamon.dialogos;

/**
 * Clase que guarda los textos de los diferentes finales de la batalla.
 */
public class DialogosFin {

    private final String[] lineasVictoria = {
            "¡¡CIBER FRANCO DERROTADO!!",
            "",
            "La Constitución ha sido recuperada.",
            "Los derechos del pueblo",
            "vuelven a estar protegidos.",
            "",
            "Tú lo has hecho posible, héroe.",
            "Ciudad Memópolis te lo agradece."
    };

    private final String[] lineasDerrota = {
            "HAS CAÍDO...",
            "",
            "Ciber Franco ríe sobre tu derrota.",
            "La Constitución permanece",
            "bajo su control digital.",
            "",
            "Pero la historia no ha terminado.",
            "El pueblo seguirá luchando."
    };


    public DialogosFin() {
    }

    public String[] getLineasVictoria() {
        return lineasVictoria;
    }

    public String[] getLineasDerrota() {
        return lineasDerrota;
    }
}
