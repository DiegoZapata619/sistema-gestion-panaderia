package org.panaderia.model;

public abstract class Usuario {
    String nombre;
    String hashPassword;

    public Usuario (String nombre, String hashPassword){
        this.nombre=nombre;
        this.hashPassword=hashPassword;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
}
