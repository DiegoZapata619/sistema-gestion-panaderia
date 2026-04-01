package org.panaderia.model;

public class Producto {
    private double precio;
    private String nombre;
    private String categoria;
    private int stock;
    private String ID;

    //Constructor
    public Producto (double precio, String nombre,String categoria, int stock, String ID){
        this.precio=precio;
        this.nombre=nombre;
        this.categoria=categoria;
        this.stock=stock;
        this.ID=ID;
    }

    //Getters y Setters para cada atributo
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public boolean productoDisponible (){
        return stock>0;
    }
}
