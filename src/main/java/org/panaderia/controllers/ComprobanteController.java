package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.panaderia.model.Venta;
import org.panaderia.model.DetalleVenta;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ComprobanteController extends MenuController implements Initializable {
    
    @FXML private Label lblIdVenta;
    
    @FXML private Label lblFecha;
    
    @FXML private Label lblCliente;
    
    @FXML private Label lblEmpleado;
    
    @FXML private Label lblMetodoPago;
    
    @FXML private Label lblSubtotal;
    
    @FXML private Label lblDescuento;
    
    @FXML private Label lblTotal;
    
    @FXML private VBox contenedorProductos;
    
    private Venta ventaActual;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Por ahora sin datos, se cargarán cuando se pase la venta
    }
    
    public void setVenta(Venta venta) {
        this.ventaActual = venta;
        cargarDatosVenta();
    }
    
    private void cargarDatosVenta() {
        if (ventaActual == null) return;
        
        // Información general
        lblIdVenta.setText(ventaActual.getIdVenta());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        lblFecha.setText(dateFormat.format(ventaActual.getFecha()));
        
        lblCliente.setText(ventaActual.getCliente().getNombre());
        lblEmpleado.setText(ventaActual.getEmpleado().getNombre());
        lblMetodoPago.setText(ventaActual.getMetodoPago().toString());
        
        // Cargar detalles de productos
        contenedorProductos.getChildren().clear();
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            VBox productoBox = new VBox(2);
            
            Label productoLabel = new Label(detalle.getProducto().getNombre() + 
                    " x" + detalle.getCantidad());
            productoLabel.setStyle("-fx-font-weight: bold;");
            
            Label precioLabel = new Label(String.format("$%.2f c/u = $%.2f", 
                    detalle.getPrecioUnitario(), detalle.getSubtotal()));
            
            productoBox.getChildren().addAll(productoLabel, precioLabel);
            contenedorProductos.getChildren().add(productoBox);
        }
        
        // Resumen financiero
        double subtotal = ventaActual.calcularSubtotal().doubleValue();
        double descuento = ventaActual.getDescuentoAplicado().doubleValue();
        double total = ventaActual.calcularTotal().doubleValue();
        
        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblDescuento.setText(String.format("$%.2f", descuento));
        lblTotal.setText(String.format("$%.2f", total));
    }
    
    @FXML
    public void imprimirComprobante() {
        try {
            // Generar archivo de texto del comprobante
            String nombreArchivo = "comprobante_" + ventaActual.getIdVenta() + ".txt";
            String ruta = System.getProperty("user.dir") + "/comprobantes/" + nombreArchivo;
            
            java.io.File directorio = new java.io.File(System.getProperty("user.dir") + "/comprobantes");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(ruta, "UTF-8")) {
                writer.println("==========================================");
                writer.println("           COMPROBANTE DE VENTA           ");
                writer.println("==========================================");
                writer.println();
                writer.println("ID Venta: " + ventaActual.getIdVenta());
                writer.println("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ventaActual.getFecha()));
                writer.println("Cliente: " + ventaActual.getCliente().getNombre());
                writer.println("Empleado: " + ventaActual.getEmpleado().getNombre());
                writer.println("Método de Pago: " + ventaActual.getMetodoPago());
                writer.println();
                writer.println("DETALLE DE PRODUCTOS:");
                writer.println("------------------------------------------");
                
                for (DetalleVenta detalle : ventaActual.getDetalles()) {
                    writer.printf("%-20s x%2d $%8.2f = $%8.2f%n",
                            detalle.getProducto().getNombre(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getSubtotal());
                }
                
                writer.println("------------------------------------------");
                writer.printf("%-30s $%8.2f%n", "Subtotal:", ventaActual.calcularSubtotal());
                writer.printf("%-30s $%8.2f%n", "Descuento:", ventaActual.getDescuentoAplicado());
                writer.printf("%-30s $%8.2f%n", "TOTAL:", ventaActual.calcularTotal());
                writer.println("==========================================");
                writer.println("      ¡Gracias por su compra!      ");
                writer.println("==========================================");
            }
            
            setAlert(javafx.scene.control.Alert.AlertType.INFORMATION, 
                    "Comprobante guardado en: " + ruta);
            
        } catch (Exception e) {
            setAlert(javafx.scene.control.Alert.AlertType.ERROR, 
                    "Error al imprimir comprobante: " + e.getMessage());
        }
    }
    
    @FXML
    public void cerrarComprobante() {
        Stage stage = (Stage) lblIdVenta.getScene().getWindow();
        stage.close();
    }
}
