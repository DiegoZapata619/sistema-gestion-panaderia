package org.panaderia.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.panaderia.model.Cliente;
import org.panaderia.model.ClienteDAO;

import java.io.IOException;

public class ClienteController {

    @FXML private TableView<Cliente> tablaClientes;

    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, Integer> colPuntos;
    @FXML private TableColumn<Cliente, Integer> colVisitas;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colPreferencias;

    @FXML private TextField txtId, txtNombre, txtTelefono, txtCorreo, txtPreferencias;

    private final ClienteDAO dao = new ClienteDAO();
    private final String RUTA = "Clientes.csv";

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos"));
        colVisitas.setCellValueFactory(new PropertyValueFactory<>("visitas"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colPreferencias.setCellValueFactory(new PropertyValueFactory<>("preferencias"));

        cargarTabla();
    }

    private void cargarTabla() {
        try {
            tablaClientes.setItems(FXCollections.observableArrayList(dao.leer(RUTA)));
        } catch (IOException e) {
            mostrarAlerta("Error al cargar clientes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void agregarCliente() {

        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta("El nombre es obligatorio", Alert.AlertType.WARNING);
            return;
        }

        Cliente nuevo = new Cliente(
                txtId.getText(),
                txtNombre.getText(),
                txtTelefono.getText(),
                txtCorreo.getText(),
                txtPreferencias.getText(),
                0, 0
        );

        try {
            dao.agregarCliente(RUTA, nuevo);
            cargarTabla();
            limpiarCampos();

            mostrarAlerta("Cliente agregado correctamente", Alert.AlertType.INFORMATION);

        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException e) {
            mostrarAlerta("Error al guardar cliente", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarCliente() {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un cliente", Alert.AlertType.WARNING);
            return;
        }

        try {
            dao.eliminarCliente(RUTA, seleccionado.getId());
            cargarTabla();

        } catch (IOException e) {
            mostrarAlerta("Error al eliminar", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void seleccionarFila() {
        Cliente c = tablaClientes.getSelectionModel().getSelectedItem();

        if (c != null) {
            txtId.setText(c.getId());
            txtNombre.setText(c.getNombre());
            txtTelefono.setText(c.getTelefono());
            txtCorreo.setText(c.getCorreo());
            txtPreferencias.setText(c.getPreferencias());
        }
    }

    @FXML
    public void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtPreferencias.clear();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
