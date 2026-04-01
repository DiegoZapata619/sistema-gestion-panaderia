package org.panaderia.model;

//Clase abstracta: No se instancia directamente un usuario. Los roles en el sistema son
//empleado y administrador y estos 2 poseen una vista con funciones distintas en la aplicacion
public abstract class Usuario {
    String nombre;
    String hashPassword;
    Rol rol;

    public Usuario (String nombre, String hashPassword, Rol rol){
        this.nombre=nombre;
        this.hashPassword=hashPassword;
        this.rol=rol;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
