package org.panaderia.model.Descuentos;

import org.panaderia.model.Venta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.panaderia.model.Descuentos.ValidadorCampos.validarPorcentaje;

public class DescuentoCategoria implements IEstrategiaDescuento{
    private final String categoria;
    private final BigDecimal porcentaje;


    public DescuentoCategoria (String categoria, BigDecimal porcentaje){
        if (categoria==null || categoria.isBlank()){
            throw new IllegalArgumentException("La categoría no puede estar vacía.");
        }
        validarPorcentaje(porcentaje);
        this.categoria=categoria;
        this.porcentaje=porcentaje;
    }

    @Override
    public BigDecimal aplicarDescuento(Venta venta) {
        return venta.getDetalles().stream()
                .filter(p -> p.getProducto().getCategoria().equalsIgnoreCase(categoria))
                .map(p -> p.getSubtotal().multiply(porcentaje))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescripcionDescuento() {
        return String.format("Descuento de %.0f%% en categoría %s.",
                porcentaje.multiply(BigDecimal.valueOf(100)).doubleValue(),categoria);
    }

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.CATEGORIA;
    }

    @Override
    public String getParametro1() {
        return categoria;
    }

    @Override
    public String getParametro2() {
        return porcentaje.toPlainString();
    }
}
