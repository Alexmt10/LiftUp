package com.iescamas.liftup.pojo;

import java.io.Serializable;
import java.util.List;

public class ItemComida implements Serializable {
    private String id;
    private String nombreComida;
    private List<ItemAlimento> alimentos;

    public ItemComida(String nombreComida, List<ItemAlimento> alimentos) {
        this.nombreComida = nombreComida;
        this.alimentos = alimentos;
    }

    public ItemComida() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}