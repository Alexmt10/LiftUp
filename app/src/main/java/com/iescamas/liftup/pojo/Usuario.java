package com.iescamas.liftup.pojo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String idUsuario;
    private String username;
    private String nombreCompleto;
    private String imagenPerfil;
    private List<String> seguidores;
    private List<String> siguiendo;
    private String nombre;
    private String apellidos;
    private String usuario;
    private String correo;
    private String contrasena;
    private String fechaNacimiento;
    private String sexo;
    private String altura;
    private String peso;

    public Usuario(String contrasena, String usuario) {
        this.contrasena = contrasena;
        this.usuario = usuario;
    }

    public Usuario(String nombre, String apellidos, String usuario, String correo,
                   String contrasena, String fechaNacimiento, String sexo, String altura, String peso) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.altura = altura;
        this.peso = peso;
    }

    public Usuario() {}

    public Usuario(String username, String nombreCompleto, String imagenPerfil) {
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.imagenPerfil = imagenPerfil;
        this.seguidores = new ArrayList<>();
        this.siguiendo = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public List<String> getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(List<String> seguidores) {
        this.seguidores = seguidores;
    }

    public List<String> getSiguiendo() {
        return siguiendo;
    }

    public void setSiguiendo(List<String> siguiendo) {
        this.siguiendo = siguiendo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", usuario='" + usuario + '\'' +
                ", correo='" + correo + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", sexo='" + sexo + '\'' +
                ", altura='" + altura + '\'' +
                ", peso='" + peso + '\'' +
                '}';
    }
}
