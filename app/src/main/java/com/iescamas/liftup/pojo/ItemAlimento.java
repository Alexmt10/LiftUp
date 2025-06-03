package com.iescamas.liftup.pojo;

import java.io.Serializable;

public class ItemAlimento implements Serializable {

    private String nombre;
    private double gramos;
    private double calorias;
    private double proteinas;
    private double grasas;
    private double carbohidratos;

    public ItemAlimento(String nombre, double gramos, double calorias, double proteinas, double grasas, double carbohidratos) {
        this.nombre = nombre;
        this.gramos = gramos;
        this.calorias = calorias;
        this.proteinas = proteinas;
        this.grasas = grasas;
        this.carbohidratos = carbohidratos;
    }

    public ItemAlimento() {
    }

    public double getCarbohidratos() {
        return carbohidratos;
    }

    public void setCarbohidratos(double carbohidratos) {
        this.carbohidratos = carbohidratos;
    }

    public double getGrasas() {
        return grasas;
    }

    public void setGrasas(double grasas) {
        this.grasas = grasas;
    }

    public double getProteinas() {
        return proteinas;
    }

    public void setProteinas(double proteinas) {
        this.proteinas = proteinas;
    }

    public double getCalorias() {
        return calorias;
    }

    public void setCalorias(double calorias) {
        this.calorias = calorias;
    }

    public double getGramos() {
        return gramos;
    }

    public void setGramos(double gramos) {
        this.gramos = gramos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
