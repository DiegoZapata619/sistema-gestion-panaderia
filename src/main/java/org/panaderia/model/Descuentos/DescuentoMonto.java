package org.panaderia.model.Descuentos;

import org.panaderia.model.Venta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.panaderia.model.Descuentos.ValidadorCampos.validarPorcentaje;

public class DescuentoMonto implements IEstrategiaDescuento {
    private final BigDecimal montoMinimo;
    private final BigDecimal porcentaje;

    public DescuentoMonto(BigDecimal montoMinimo, BigDecimal porcentaje) {
        if (montoMinimo == null || montoMinimo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto debe ser superior a 0.");
        }
        validarPorcentaje(porcentaje);
        this.montoMinimo = montoMinimo;
        this.porcentaje = porcentaje;

    }

    @Override
    public BigDecimal aplicarDescuento(Venta venta) {
        BigDecimal subtotal = venta.calcularSubtotal();
        if (subtotal.compareTo(montoMinimo) < 0) return BigDecimal.ZERO;
        return subtotal.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescripcionDescuento() {

        return String.format("Descuento de %.0f%% en compras mayores de $%.0f",
                porcentaje.multiply(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)).doubleValue(),
                montoMinimo.toPlainString());
    }

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.MONTO;
    }

    @Override
    public String getParametro1() {
        return montoMinimo.toPlainString();
    }

    @Override
    public String getParametro2() {
        return porcentaje.toPlainString();
    }

}