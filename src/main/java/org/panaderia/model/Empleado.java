package org.panaderia.model;

public class Empleado extends Usuario{
    private String idEmpleado;
    public Empleado (String idEmpleado,String nombre, String hashPassword){
        super(nombre,hashPassword);
        this.idEmpleado=idEmpleado;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
