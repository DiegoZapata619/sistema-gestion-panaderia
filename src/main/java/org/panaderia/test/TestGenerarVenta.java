package org.panaderia.test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class TestGenerarVenta {
    public static void main(String[] args) {
        try {
            System.out.println("=== PRUEBA DE CARGA DE VISTA GENERAR VENTA ===");
            
            // Intentar cargar la vista
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TestGenerarVenta.class.getResource("/views/generarVenta.fxml"));
            Parent root = loader.load();
            
            System.out.println("✅ Vista generarVenta.fxml cargada exitosamente");
            
            // Verificar que el controlador se cargó
            Object controller = loader.getController();
            if (controller != null) {
                System.out.println("✅ Controlador GenerarVentaController cargado exitosamente");
                System.out.println("   Clase: " + controller.getClass().getSimpleName());
            } else {
                System.out.println("❌ Error: El controlador es nulo");
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la vista: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
