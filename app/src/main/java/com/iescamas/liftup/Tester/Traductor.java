package com.iescamas.liftup.Tester;

import java.util.Locale;

public class Traductor {


    public String traducirTexto(String texto, Locale locale) {
        if (locale.equals(Locale.FRENCH)) {
            if (texto.equalsIgnoreCase("hola")) return "bonjour";
        } else if (locale.equals(Locale.ENGLISH)) {
            if (texto.equalsIgnoreCase("hola")) return "hello";
        }
        return texto;
    }
}
