package com.iescamas.liftup.pojo;

import java.util.Date;

public class ItemMensajesGrupo {

    private String emisorId;
    private String texto;
    private Date fecha;
    private String grupoId;
    private String emisorUsername; // NUEVO campo

    public ItemMensajesGrupo() {
    }

    public ItemMensajesGrupo(String emisorId, String texto, Date fecha, String grupoId) {
        this.emisorId = emisorId;
        this.texto = texto;
        this.fecha = fecha;
        this.grupoId = grupoId;
    }

    public String getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(String emisorId) {
        this.emisorId = emisorId;
    }

    public String getTexto() {
        return texto;
    }



    public String getGrupoId() {
        return grupoId;
    }


    public String getEmisorUsername() {
        return emisorUsername;
    }

    public void setEmisorUsername(String emisorUsername) {
        this.emisorUsername = emisorUsername;
    }
}
