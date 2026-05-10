package org.panaderia.model.Descuentos;

import java.math.BigDecimal;

public class DescuentoFactory {

    /// Metodo generico para crear estrategias usado por PromocionDAO
    /// Param1 es una ID, categoria o monto
    /// Param2 es el porcentaje aplicado

    public static IEstrategiaDescuento crear (TipoDescuento tipo, String param1, String param2){
        return switch (tipo){
            case PRODUCTO -> crearEstrategiaProducto(param1,param2);
            case MONTO -> crearEstrategiaMonto(param1,param2);
            case CLIENTE -> crearEstrategiaCliente(param1,param2);
            case CATEGORIA -> crearEstrategiaCategoria(param1,param2);
        };
    }

    /// Metodos para crear cada tipo de descuento
    /// Cada tipo de descuento cuenta con 2 parámetros: El porcentaje y una string que define
    /// la característica principal, que puede ser la ID de un cliente o producto, una categoría
    /// o un monto mínimo que se convierte en BigDecimal

    public static IEstrategiaDescuento crearEstrategiaProducto (String productoID, String porcentaje){
        validarID(productoID, "producto");
        return new DescuentoProducto(productoID,parsePorcentaje(porcentaje));
    }
    public static IEstrategiaDescuento crearEstrategiaCliente (String clienteID, String porcentaje){
        validarID(clienteID, "cliente");
        return new DescuentoCliente(clienteID,parsePorcentaje(porcentaje));
    }

    /// Es mejor crear el BigDecimal con String para evitar errores de precisión
    public static IEstrategiaDescuento crearEstrategiaMonto (String montoMinimo, String porcentaje){
        BigDecimal monto= new BigDecimal(montoMinimo);
        if (monto.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        return new DescuentoMonto(monto, parsePorcentaje(porcentaje));
    }

    public static IEstrategiaDescuento crearEstrategiaCategoria (String categoria, String porcentaje){
        if (categoria==null || categoria.isBlank()){
            throw new IllegalArgumentException("La categoría no puede ser vacía");
        }
        return new DescuentoCategoria(categoria,parsePorcentaje(porcentaje));
    }

    /// Metodo para crear porcentajes con String, lo que resuelve problemas de precision
    /// new BigDecimal ("0.1") = 0.1 exactamente

    public static BigDecimal parsePorcentaje (String porcentaje){
        if (porcentaje== null || porcentaje.isBlank()){
            throw  new IllegalArgumentException("El porcentaje no puede ser vacio.");
        }
        BigDecimal porcentajeReal = new BigDecimal(porcentaje);
        if (porcentajeReal.compareTo(BigDecimal.ZERO)<0 || porcentajeReal.compareTo(BigDecimal.ONE)>0){
            throw new IllegalArgumentException("El porcentaje debe estar entre 0.0 y 1.0.");
        }
        return porcentajeReal;
    }

/// Metodo para validar una ID
    public static void validarID(String id, String tipo){
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("El ID de " + tipo + " no puede estar vacío");
    }
}
