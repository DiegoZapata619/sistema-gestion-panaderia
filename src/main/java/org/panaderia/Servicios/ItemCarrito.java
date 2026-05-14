package org.panaderia.Servicios;

import org.panaderia.model.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemCarrito {
    private final Producto productoRef;
    private final String idProducto;
    private final String nombreProducto;
    private final BigDecimal precioUnitario;
    private int cantidad;
    private BigDecimal subtotal;

    public ItemCarrito(Producto producto, int cantidad) {
        this.productoRef = producto;
        this.idProducto = producto.getId();
        this.nombreProducto = producto.getNombre();
        this.precioUnitario = BigDecimal.valueOf(producto.getPrecio())
                .setScale(2, RoundingMode.HALF_UP);
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public void actualizarSubtotal() {
        this.subtotal = precioUnitario
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public Producto getProductoRef() {
        return productoRef;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
