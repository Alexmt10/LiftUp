// PlanComida.java
package com.iescamas.liftup.pojo;

import java.io.Serializable;
import java.util.List;

public class PlanComida  implements Serializable {

    private String id;
    private String nombre;
    private List<ItemAlimento> alimentos;

    public PlanComida(String nombre, List<ItemAlimento> alimentos) {
        this.nombre = nombre;
        this.alimentos = alimentos;
    }

    public PlanComida(String id, String nombre, List<ItemAlimento> alimentos) {
        this.id = id;
        this.nombre = nombre;
        this.alimentos = alimentos;
    }

    public PlanComida() {
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public List<ItemAlimento> getAlimentos() {
        return alimentos;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setAlimentos(List<ItemAlimento> alimentos) {
        this.alimentos = alimentos;
    }
}