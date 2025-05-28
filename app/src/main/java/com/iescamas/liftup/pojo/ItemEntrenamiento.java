package com.iescamas.liftup.pojo;

import java.util.List;

public class ItemEntrenamiento {

    private String id;
    private String nombreEntrenamiento;
    private int imagenEntrenamiento;
    private List<ItemEntrenoCompleto> ejercicios;

    public ItemEntrenamiento(String id, String nombreEntrenamiento, int imagenEntrenamiento, List<ItemEntrenoCompleto> ejercicios) {
        this.id = id;
        this.nombreEntrenamiento = nombreEntrenamiento;
        this.imagenEntrenamiento = imagenEntrenamiento;
        this.ejercicios = ejercicios;
    }

    public ItemEntrenamiento() {
    }

    public ItemEntrenamiento(String nombreEntrenamiento) {
        this.nombreEntrenamiento = nombreEntrenamiento;
    }

    public String getNombreEntrenamiento() {
        return nombreEntrenamiento;
    }

    public void setNombreEntrenamiento(String nombreEntrenamiento) {
        this.nombreEntrenamiento = nombreEntrenamiento;
    }

    public int getImagenEntrenamiento() {
        return imagenEntrenamiento;
    }

    public void setImagenEntrenamiento(int imagenEntrenamiento) {
        this.imagenEntrenamiento = imagenEntrenamiento;
    }

    public List<ItemEntrenoCompleto> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<ItemEntrenoCompleto> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
