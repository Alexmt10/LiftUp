package com.iescamas.liftup.pojo;

public class ItemSerie {

    public int repeticiones;
    public int peso;

    public ItemSerie(int repeticiones, int peso) {
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    public ItemSerie() {
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }
}
