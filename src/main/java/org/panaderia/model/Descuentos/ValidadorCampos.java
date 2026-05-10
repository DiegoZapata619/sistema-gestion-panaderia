package org.panaderia.model.Descuentos;

import java.math.BigDecimal;

public class ValidadorCampos {
    /// Metodo para verificar que el porcentaje se encuentre en rango 0.0 y 1.0
    public static void validarPorcentaje (BigDecimal porcentaje){
        if (porcentaje == null ||
                porcentaje.compareTo(BigDecimal.ZERO) < 0 ||
                porcentaje.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Porcentaje inválido");
        }
    }
}
