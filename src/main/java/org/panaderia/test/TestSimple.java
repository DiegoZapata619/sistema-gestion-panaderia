package org.panaderia.test;

import org.panaderia.DAO.ProductDAO;
import org.panaderia.DAO.VentaDAO;
import org.panaderia.model.Venta;

import java.io.IOException;
import java.util.List;

public class TestSimple {
    public static void main(String[] args) {
        try {
            System.out.println("=== PRUEBA SIMPLE ===");
            
            // Probar ProductDAO primero
            System.out.println("1. Probando ProductDAO...");
            ProductDAO productDAO = new ProductDAO();
            List productos = productDAO.obtenerTodos();
            System.out.println("Productos leídos: " + productos.size());
            
            // Probar VentaDAO con ProductDAO
            System.out.println("2. Probando VentaDAO con ProductDAO...");
            VentaDAO ventaDAO = new VentaDAO(productDAO);
            String ruta = System.getProperty("user.dir") + "/data/ventas.csv";
            List<Venta> ventas = ventaDAO.leer(ruta);
            System.out.println("Ventas leídas: " + ventas.size());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
