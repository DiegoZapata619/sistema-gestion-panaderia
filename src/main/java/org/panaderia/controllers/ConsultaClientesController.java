package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.panaderia.DAO.ClienteDAO;
import org.panaderia.model.Cliente;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultaClientesController extends MenuController implements Initializable {
    
    @FXML
    private TableView<Cliente> tablaClientes;
    
    @FXML
    private TableColumn<Cliente, String> colId;
    
    @FXML
    private TableColumn<Cliente, String> colNombre;
    
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    
    @FXML
    private TableColumn<Cliente, Integer> colPuntos;
    
    @FXML
    private TableColumn<Cliente, Integer> colVisitas;
    
    private ClienteDAO clienteDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteDAO = new ClienteDAO();
        configurarTabla();
        cargarClientes();
    }
    
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos"));
        colVisitas.setCellValueFactory(new PropertyValueFactory<>("visitas"));
    }
    
    private void cargarClientes() {
        try {
            String rutaClientes = System.getProperty("user.dir") + "/data/clientes.csv";
            List<Cliente> clientes = clienteDAO.leer(rutaClientes);
            tablaClientes.getItems().clear();
            tablaClientes.getItems().addAll(clientes);
        } catch (IOException e) {
            setAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error al cargar clientes: " + e.getMessage());
        }
    }
    
    @FXML
    public void actualizarTabla() {
        cargarClientes();
    }
    
    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) tablaClientes.getScene().getWindow();
        stage.close();
    }
}
