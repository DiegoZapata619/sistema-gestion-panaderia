package org.panaderia.model.Descuentos;

import org.panaderia.model.Venta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.panaderia.model.Descuentos.ValidadorCampos.validarPorcentaje;

public class DescuentoCliente implements IEstrategiaDescuento{
    private final String clienteID;
    private final BigDecimal porcentaje;

    public DescuentoCliente (String clienteID, BigDecimal porcentaje){
        validarPorcentaje(porcentaje);
        this.porcentaje=porcentaje;
        this.clienteID=clienteID;
    }

    @Override
    public BigDecimal aplicarDescuento(Venta venta) {
        if (venta.getCliente()==null) return BigDecimal.ZERO;
        if (!venta.getCliente().getId().equals(clienteID)) return BigDecimal.ZERO;
        return venta.calcularSubtotal().
                multiply(porcentaje).
                setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescripcionDescuento() {
        return String.format("Descuento de %.0f%% en cliente %s.",
                porcentaje.multiply(BigDecimal.valueOf(100)).doubleValue(),clienteID);
    }

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.CLIENTE;
    }

    @Override
    public String getParametro1() {
        return clienteID;
    }

    @Override
    public String getParametro2() {
        return porcentaje.toPlainString();
    }

}