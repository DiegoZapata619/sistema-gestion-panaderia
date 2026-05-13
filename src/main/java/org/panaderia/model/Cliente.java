package org.panaderia.model;

public class Cliente {

    private String id;
    private String nombre;

    private String telefono;
    private String correo;
    private String preferencias;

    private int puntos;
    private int visitas;

    public Cliente(String id, String nombre, String telefono,
                   String correo, String preferencias,
                   int puntos, int visitas) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.preferencias = preferencias;
        this.puntos = puntos;
        this.visitas = visitas;
    }
    public Cliente (String id, String nombre){
        this.id=id;
        this.nombre=nombre;
        telefono=null;
        correo=null;
        preferencias=null;
        puntos=0;
        visitas=0;
    }

    // GETTERS
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getPreferencias() { return preferencias; }
    public int getPuntos() { return puntos; }
    public int getVisitas() { return visitas; }

    // SETTERS
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public void setVisitas(int visitas) { this.visitas = visitas; }
}