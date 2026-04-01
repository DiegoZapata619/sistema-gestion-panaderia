package org.panaderia.model;

public class Administrador extends Usuario{
    private String idAdministrador;

    public Administrador(String idAdministrador,String nombre, String hashPassword) {
        super (nombre,hashPassword);
        this.idAdministrador=idAdministrador;
    }

    public String getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(String idAdministrador) {
        this.idAdministrador = idAdministrador;
    }
}
