package org.panaderia.model.Descuentos;

import org.panaderia.model.Venta;

import java.math.BigDecimal;

public interface IEstrategiaDescuento {
    /// Metodos genericos de operaciones en estrategias
    BigDecimal aplicarDescuento (Venta venta);
    String getDescripcionDescuento();

    /// Metodos para persistencia en archivo .csv
    TipoDescuento getTipo();
    String getParametro1();
    String getParametro2();

}
