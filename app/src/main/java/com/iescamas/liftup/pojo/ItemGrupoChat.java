package com.iescamas.liftup.pojo;

import java.util.List;

public class ItemGrupoChat {
    private String idGrupo;
    private String nombreGrupo;
    private String imagenGrupoUrl;
    private List<String> miembros; // Lista de IDs de los usuarios que pertenecen al grupo

    public ItemGrupoChat(String id, String nombre, String iconoUrl) {
        this.idGrupo = id;
        this.nombreGrupo = nombre;
        this.imagenGrupoUrl = iconoUrl;
    }


    public ItemGrupoChat(String idGrupo, String nombreGrupo, String imagenGrupoUrl, List<String> miembros) {
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
        this.imagenGrupoUrl = imagenGrupoUrl;
        this.miembros = miembros;
    }

    // Getters y setters
    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getImagenGrupoUrl() {
        return imagenGrupoUrl;
    }

    public void setImagenGrupoUrl(String imagenGrupoUrl) {
        this.imagenGrupoUrl = imagenGrupoUrl;
    }

    public List<String> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<String> miembros) {
        this.miembros = miembros;
    }
}

