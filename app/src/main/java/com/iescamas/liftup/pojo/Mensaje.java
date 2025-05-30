package com.iescamas.liftup.pojo;

import java.util.Date;

public class Mensaje {
    private String emisor;
    private String receptor;
    private String contenido;
    private Date fecha;

    public Mensaje() {}

    public Mensaje(String emisor, String receptor, String contenido, Date fecha) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    public String getEmisor() { return emisor; }
    public String getReceptor() { return receptor; }
    public String getContenido() { return contenido; }
    public Date getFecha() { return fecha; }

    public void setEmisor(String emisor) { this.emisor = emisor; }
    public void setReceptor(String receptor) { this.receptor = receptor; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
