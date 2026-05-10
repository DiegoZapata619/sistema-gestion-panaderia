package org.panaderia.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/// Representa las filas de la tabla de productos, incluye cantidad de productos vendidos,
/// precio unitario, descuentos aplicados e idProducto.
public class DetalleVenta {
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null)
            throw new IllegalArgumentException("El producto no puede ser nulo");
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        if (!producto.productoDisponible())
            throw new IllegalStateException("El producto '" + producto.getNombre() + "' no tiene stock");

        this.producto = producto;
        this.cantidad = cantidad;
        // Se construye desde String para evitar pérdida de precisión
        this.precioUnitario = BigDecimal.valueOf(producto.getPrecio())
                .setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal getSubtotal (){
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).
                setScale(2,RoundingMode.HALF_UP);
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}
