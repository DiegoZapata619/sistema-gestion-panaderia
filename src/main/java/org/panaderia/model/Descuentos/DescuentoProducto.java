package org.panaderia.model.Descuentos;

import org.panaderia.model.Venta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.panaderia.model.Descuentos.ValidadorCampos.validarPorcentaje;

public class DescuentoProducto implements IEstrategiaDescuento {
    private final String productoID;
    private final BigDecimal porcentaje;

    public DescuentoProducto(String productoID, BigDecimal porcentaje) {
        validarPorcentaje(porcentaje);
        this.productoID = productoID;
        this.porcentaje = porcentaje;

    }

    /// Se obtienen los items asociados a la venta para aplicar descuento asociado
    @Override
    public BigDecimal aplicarDescuento(Venta venta) {
        return venta.getDetalles().stream()
                .filter(l -> l.getProducto().getId().equals(productoID))
                .map(l -> l.getSubtotal().multiply(porcentaje))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescripcionDescuento() {
        return String.format("Descuento de %.0f%% en producto: %s",
                porcentaje.multiply(BigDecimal.valueOf(100)).doubleValue(), productoID);
    }

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.PRODUCTO;
    }

    @Override
    public String getParametro1() {
        return productoID;
    }

    @Override
    public String getParametro2() {
        return porcentaje.toPlainString();
    }

}