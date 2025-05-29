package com.iescamas.liftup.pojo;

public class ItemYoPerfil {

    private String imagenPerfil;
    private ItemEntrenamiento entrenamiento;
    private PlanComida comida;


    public ItemYoPerfil() {
    }

    public ItemYoPerfil(int imagenPerfil, ItemEntrenamiento entrenamiento, PlanComida comida) {

    }


    public ItemEntrenamiento getEntrenamiento() {
        return entrenamiento;
    }

    public void setEntrenamiento(ItemEntrenamiento entrenamiento) {
        this.entrenamiento = entrenamiento;
    }

    public PlanComida getComida() {
        return comida;
    }

    public void setComida(PlanComida comida) {
        this.comida = comida;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
