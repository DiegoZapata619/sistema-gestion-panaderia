package org.panaderia.model.Descuentos;


/// Se definen 4 tipos de descuentos especiales: montos de compra, producto, cliente y categoria
/// Pueden incluirse más tipos de descuento, pero es pertinente modificar

public enum TipoDescuento {
    MONTO,
    PRODUCTO,
    CLIENTE,
    CATEGORIA;


    public static TipoDescuento convertirAValorEnum(String valor) {
        try {
            return TipoDescuento.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tipo de descuento inválido: '" + valor + "'. " +
                            "Valores aceptados: PRODUCTO, CLIENTE, MONTO, CATEGORIA");
        }
    }
}
