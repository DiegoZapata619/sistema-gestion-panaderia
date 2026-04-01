package org.panaderia.model;

public class Empleado extends Usuario{
    public Empleado (String nombre, String hashPassword, Rol rol){
        super(nombre,hashPassword, rol);
    }
}
