package org.panaderia.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.panaderia.model.Cliente;
import org.panaderia.DAO.ClienteDAO;

import java.io.IOException;

public class ClienteController extends MenuController{

    @FXML private TableView<Cliente> tablaClientes;

    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, Integer> colPuntos;
    @FXML private TableColumn<Cliente, Integer> colVisitas;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colPreferencias;

    @FXML private TextField txtId, txtNombre, txtTelefono, txtCorreo, txtPreferencias;

    private final ClienteDAO dao = new ClienteDAO();
    private final String CLIENTES_FILE =
            System.getProperty("user.dir") + "/data/clientes.csv";

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
            tablaClientes.setItems(FXCollections.observableArrayList(dao.leer(CLIENTES_FILE)));
        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error al cargar clientes. ");
        }
    }

    @FXML
    public void agregarCliente() {

        if (txtNombre.getText().isEmpty()) {
            setAlert(Alert.AlertType.ERROR, "El nombre no puede ser vacio. ");
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
            dao.agregar(CLIENTES_FILE, nuevo);
            cargarTabla();
            limpiarCampos();
            setAlert(Alert.AlertType.INFORMATION, "Cliente agregado correctamente. ");


        } catch (IllegalArgumentException e) {
            setAlert(Alert.AlertType.ERROR, "Uno de los campos posee información no válida. ");
        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error al agregar cliente. ");
        }
    }

    @FXML
    public void eliminarCliente() {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            setAlert(Alert.AlertType.WARNING, "Debes seleccionar un cliente. ");
            return;
        }

        try {
            dao.eliminar(CLIENTES_FILE, seleccionado.getId());
            cargarTabla();
            setAlert(Alert.AlertType.INFORMATION, "Cliente eliminado correctamente. ");

        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error al eliminar cliente.");
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

    public void limpiarCampos (){
        limpiarCeldas(txtId,txtNombre,txtTelefono,txtCorreo,txtPreferencias);
    }

}
