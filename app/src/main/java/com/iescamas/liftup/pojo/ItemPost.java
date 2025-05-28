package com.iescamas.liftup.pojo;

import java.io.Serializable;

public class ItemPost implements Serializable {

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
        this.idUsuario = uidUsuario;
        this.imagenPostUrl = imagenPostUrl;
        this.entrenamiento = entrenamiento;
        this.comida = comida;
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getImagenUsuario() {
        return imagenUsuario;
    }

    public void setImagenUsuario(int imagenUsuario) {
        this.imagenUsuario = imagenUsuario;
    }

    public int getImagenPost() {
        return imagenPost;
    }

    public void setImagenPost(int imagenPost) {
        this.imagenPost = imagenPost;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComentarios() {
        return comentarios;
    }

    public void setComentarios(int comentarios) {
        this.comentarios = comentarios;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getImagenPostUrl() {
        return imagenPostUrl;
    }

    public void setImagenPostUrl(String imagenPostUrl) {
        this.imagenPostUrl = imagenPostUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNumero_megusta() {
        return numero_megusta;
    }

    public void setNumero_megusta(int numero_megusta) {
        this.numero_megusta = numero_megusta;
    }
}
