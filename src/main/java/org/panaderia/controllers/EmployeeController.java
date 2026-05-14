package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.panaderia.model.Sesion;

public class EmployeeController extends MenuController{
    
    @FXML
    /// TextFiel para obtener el stage actual
    private TextField txtField;
    
    @FXML
    public void abrirGenerarVenta() {
        openNewStage(VENTA_VIEW, titulosFxml.get(VENTA_VIEW), getCurrentStage(), EMPLOYEE_VIEW);
    }
    
    @FXML
    public void abrirConsultaProductos() {
        openNewStage(CONSULTAP_VIEW, titulosFxml.get(CONSULTAP_VIEW), getCurrentStage(), EMPLOYEE_VIEW);
    }
    
    @FXML
    public void abrirConsultaClientes() {
        openNewStage(CONSULTAC_VIEW, titulosFxml.get(CONSULTAC_VIEW), getCurrentStage(), EMPLOYEE_VIEW);
    }
    
    @FXML
    public void cerrarSesion() {
        openNewStage(LOGIN_VIEW, titulosFxml.get(LOGIN_VIEW), null, null);
        Sesion.cerrar();
    }
    
    private Stage getCurrentStage() {
        if (txtField != null) {
            return (Stage) txtField.getScene().getWindow();
        }
        return null;
    }
}
