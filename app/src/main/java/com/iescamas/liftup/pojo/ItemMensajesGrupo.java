package com.iescamas.liftup.pojo;

import java.util.Date;

public class ItemMensajesGrupo {  private String emisorId;
    private String texto;
    private Date fecha;
    private String grupoId;

    public ItemMensajesGrupo() {}


    public ItemMensajesGrupo(String emisorId, String texto, Date fecha, String grupoId) {
        this.emisorId = emisorId;
        this.texto = texto;
        this.fecha = fecha;
        this.grupoId = grupoId;
    }

    public String getEmisorId() { return emisorId; }
    public String getTexto() { return texto; }
    public Date getFecha() { return fecha; }
    public String getGrupoId() { return grupoId; }

    public void setEmisorId(String emisorId) {
        this.emisorId = emisorId;
    }

    public void setGrupoId(String grupoId) {
        this.grupoId = grupoId;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}