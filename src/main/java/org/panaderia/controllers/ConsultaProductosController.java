package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.panaderia.DAO.ProductDAO;
import org.panaderia.model.Producto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultaProductosController extends MenuController implements Initializable {
    
    @FXML
    private TableView<Producto> tablaProductos;
    
    @FXML
    private TableColumn<Producto, String> colId;
    
    @FXML
    private TableColumn<Producto, String> colNombre;
    
    @FXML
    private TableColumn<Producto, String> colCategoria;
    
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    
    @FXML
    private TableColumn<Producto, Integer> colStock;
    
    @FXML
    private TableColumn<Producto, String> colDescripcion;
    
    private ProductDAO productDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productDAO = new ProductDAO();
        configurarTabla();
        cargarProductos();
    }
    
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }
    
    private void cargarProductos() {
        try {
            List<Producto> productos = productDAO.obtenerTodos();
            tablaProductos.getItems().clear();
            tablaProductos.getItems().addAll(productos);
        } catch (IOException e) {
            setAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error al cargar productos: " + e.getMessage());
        }
    }
    
    @FXML
    public void actualizarTabla() {
        cargarProductos();
    }
    
    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) tablaProductos.getScene().getWindow();
        stage.close();
    }
}
