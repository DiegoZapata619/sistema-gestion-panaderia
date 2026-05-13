package org.panaderia.test;

import org.panaderia.DAO.VentaDAO;
import org.panaderia.model.Venta;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestVentasDAO {
    public static void main(String[] args) {
        VentaDAO dao = new VentaDAO();
        
        try {
            System.out.println("=== PRUEBA DE LECTURA DE VENTAS ===");
            String ruta = System.getProperty("user.dir") + "/data/ventas.csv";
            System.out.println("Ruta del archivo: " + ruta);
            
            File archivo = new File(ruta);
            System.out.println("¿Existe el archivo? " + archivo.exists());
            System.out.println("Ruta absoluta: " + archivo.getAbsolutePath());
            
            List<Venta> ventas = dao.leer(ruta);
            
            System.out.println("Ventas leídas: " + ventas.size());
            
            for (Venta venta : ventas) {
                System.out.println("\n--- Venta ---");
                System.out.println("ID: " + venta.getIdVenta());
                System.out.println("Fecha: " + venta.getFecha());
                System.out.println("Cliente: " + (venta.getCliente() != null ? venta.getCliente().getNombre() : "NULL"));
                System.out.println("Empleado: " + (venta.getEmpleado() != null ? venta.getEmpleado().getNombre() : "NULL"));
                System.out.println("Método Pago: " + venta.getMetodoPago());
                System.out.println("Total: $" + venta.calcularTotal());
                System.out.println("Detalles: " + venta.getDetalles().size());
                
                venta.getDetalles().forEach(detalle -> {
                    System.out.println("  - " + detalle.getProducto().getNombre() + 
                                     " x" + detalle.getCantidad() + 
                                     " = $" + detalle.getSubtotal());
                });
            }
            
        } catch (IOException e) {
            System.err.println("Error al leer ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
