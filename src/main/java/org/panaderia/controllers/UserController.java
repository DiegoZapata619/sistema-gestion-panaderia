package org.panaderia.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.panaderia.DAO.UsuarioDAO;
import org.panaderia.Servicios.Encriptador;
import org.panaderia.model.*;
import org.panaderia.model.Descuentos.TipoDescuento;
import org.panaderia.Servicios.Encriptador;

import java.io.IOException;

public class UserController extends MenuController{

    private UsuarioDAO userDao= new UsuarioDAO();
    private final String RUTA = "Users.csv";
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colPassword;
    @FXML private TableColumn<Usuario, Rol> colRol;

    @FXML private TextField txtNombre, txtPassword;
    @FXML private ComboBox<Rol> combRol;


    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        combRol.setItems(FXCollections.observableArrayList(Rol.values()));
        cargarTabla();
    }

    public void cargarTabla (){
        try {
            tablaUsuarios.setItems(FXCollections.
                    observableArrayList(userDao.leer(RUTA)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void agregarUsuario (){
        if (celdasVacias(txtNombre, txtPassword)){
            setAlert(Alert.AlertType.WARNING, "Alguno de los campos está vacío.");
        }
        if (combRol.getValue()==null){
            setAlert(Alert.AlertType.WARNING, "Debes seleccionar un rol");
            return;
        }
        String nombre= txtNombre.getText();
        String hash = Encriptador.sha256(txtPassword.getText());
        Rol rol= combRol.getValue();

        Usuario nuevo= (rol==Rol.ADMINISTRADOR)? new Administrador(nombre,hash,rol)
                : new Empleado(nombre,hash,rol);

        try {
            userDao.agregar(RUTA,nuevo);
            cargarTabla();
            limpiarCampos();

        } catch (IllegalArgumentException e){
            setAlert(Alert.AlertType.ERROR, "Información no valida en alguno de los campos. ");

        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error al agregar usuario. ");
        }
    }
    @FXML
    public void eliminarUsuario(){
        Usuario seleccionado= tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado==null){
            setAlert(Alert.AlertType.ERROR, "No se ha seleccionado un usuario a eliminar.");
        }
        try {
            boolean eliminado = userDao.eliminar(RUTA, seleccionado.getNombre());
            if (eliminado){
                cargarTabla();
                limpiarCampos();
            } else {
                setAlert(Alert.AlertType.ERROR, "No se encontró al usuario a eliminar. ");
            }
        } catch (IOException e){
            setAlert(Alert.AlertType.ERROR, "Error al eliminar el usuario. ");
        }
    }
    @FXML
    public void actualizarUsuario (){
        String nombre= txtNombre.getText();
        String hash=  Encriptador.sha256(txtPassword.getText());
        Rol rol = combRol.getValue();
        Usuario actualizado= (rol==Rol.ADMINISTRADOR)? new Administrador(nombre,hash,rol)
                : new Empleado(nombre,hash,rol);
        try {
            boolean actualizadoCorrectamente = userDao.actualizar(RUTA, actualizado);
            if (actualizadoCorrectamente){
                cargarTabla();
                limpiarCampos();
            }
            else {
                setAlert(Alert.AlertType.WARNING, "No se encontró el usuario a actualizar. ");
            }

        } catch (IOException e){
            setAlert(Alert.AlertType.ERROR, "Uno de los campos posee un dato erróneo. ");


        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al actualizar usuario. ");
        }
    }

    @FXML
    public void limpiarCampos(){
        limpiarCeldas(txtNombre,txtPassword);
        combRol.setValue(null);
        tablaUsuarios.getSelectionModel().clearSelection();

    }
    @FXML
    public void seleccionarFila (){
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado!=null){
            txtNombre.setText(seleccionado.getNombre());
            txtPassword.setText(seleccionado.getPassword());
            combRol.setValue(seleccionado.getRol());
            }
        }
    }
