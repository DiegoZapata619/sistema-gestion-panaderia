package org.panaderia.model;

public class Empleado extends Usuario{
    public Empleado (String nombre, String hashPassword, Rol rol){
        super(nombre,hashPassword, rol);
    }
    public Empleado(String nombre, Rol rol){
        super(nombre,rol);

    }
}
