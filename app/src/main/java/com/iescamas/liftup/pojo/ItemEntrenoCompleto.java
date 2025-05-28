package com.iescamas.liftup.pojo;

import java.util.List;

public class ItemEntrenoCompleto {

    String musculo;
    String ejercicio;
    List<ItemSerie> series;

    public ItemEntrenoCompleto(String musculo, String ejercicio, List<ItemSerie> series) {
        this.musculo = musculo;
        this.ejercicio = ejercicio;
        this.series = series;
    }
    public ItemEntrenoCompleto() {
    }

    public String getMusculo() {
        return musculo;
    }

    public void setMusculo(String musculo) {
        this.musculo = musculo;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }

    public List<ItemSerie> getSeries() {
        return series;
    }

    public void setSeries(List<ItemSerie> series) {
        this.series = series;
    }
}
