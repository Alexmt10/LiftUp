package com.iescamas.liftup.pojo;

import java.io.Serializable;

public class ItemPost implements Serializable {
    private String uidUsuario;


    private String idUsuario;
    private String nombreUsuario;
    private int imagenUsuario;
    private int imagenPost;
    private int likes;
    private int comentarios;
    private ItemEntrenamiento entrenamiento;
    private PlanComida comida;
    private String descripcion;
    private int numero_megusta;
    private String imagenPostUrl;


    public ItemPost(String userName, String descripcion, String userId, ItemEntrenamiento planEntrenamientoSeleccionado, PlanComida planComidaSeleccionado) {
    }

    public ItemPost() {
    }



    public ItemPost(String nombreUsuario, String descripcion, String uidUsuario, String imagenPostUrl, ItemEntrenamiento entrenamiento, PlanComida comida) {
        this.nombreUsuario = nombreUsuario;
        this.descripcion = descripcion;
        this.uidUsuario = uidUsuario;
        this.imagenPostUrl = imagenPostUrl;
        this.entrenamiento = entrenamiento;
        this.comida = comida;
    }

    public ItemPost(int imagenPost, ItemEntrenamiento entrenamiento, PlanComida comida) {

    }

    public ItemPost(String nombreUsuario, int imagenUsuario, int imagenPost, int likes, int comentarios, String descripcion) {
        this.nombreUsuario = nombreUsuario;
        this.imagenUsuario = imagenUsuario;
        this.imagenPost = imagenPost;
        this.likes = likes;
        this.comentarios = comentarios;
        this.descripcion = descripcion;
    }

    public ItemPost(String nombreUsuario, int imagenUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.imagenUsuario = imagenUsuario;
    }

    public ItemPost(String nombreUsuario, int imagenPost, String descripcion) {
        this.nombreUsuario = nombreUsuario;
        this.imagenPost = imagenPost;
        this.descripcion = descripcion;
    }

    public ItemPost(String nombreUsuario, int imagenUsuario, int imagenPost) {
        this.nombreUsuario = nombreUsuario;
        this.imagenUsuario = imagenUsuario;
        this.imagenPost = imagenPost;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }


    public int getImagenPost() {
        return imagenPost;
    }



    public ItemEntrenamiento getEntrenamiento() {
        return entrenamiento;
    }


    public PlanComida getComida() {
        return comida;
    }

    public void setComida(PlanComida comida) {
        this.comida = comida;
    }

    public String getImagenPostUrl() {
        return imagenPostUrl;
    }


    public String getDescripcion() {
        return descripcion;
    }

}
