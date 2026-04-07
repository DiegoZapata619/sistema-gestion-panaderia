package org.panaderia.model;

public class Producto {
    private String id;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private int stockMinimo;
    private String descripcion;

    //Constructor
    public Producto (String id, String nombre,String categoria,
                     double precio, int stock, int stockMinimo, String descripcion){
        this.id =id;
        this.nombre=nombre;
        this.categoria=categoria;
        this.precio=precio;
        this.stock=stock;
        this.stockMinimo=stockMinimo;
        this.descripcion=descripcion;
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

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    //Metodo que determinad si hay stock del producto. Si es mayor a 0, quiere decir que sigue disponible
    public boolean productoDisponible (){
        return stock>0;
    }
}
