package org.panaderia.model;

public class Administrador extends Usuario{

    public Administrador(String nombre, String hashPassword, Rol rol) {
        super (nombre,hashPassword,rol);
    }
}
