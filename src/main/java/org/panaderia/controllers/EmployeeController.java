package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmployeeController extends MenuController{
    
    @FXML
    private TextField txtFieldDummy; // Campo dummy para obtener el stage
    
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
        openNewStage(LOGIN_VIEW, titulosFxml.get(LOGIN_VIEW), getCurrentStage(), null);
    }
    
    private Stage getCurrentStage() {
        if (txtFieldDummy != null) {
            return (Stage) txtFieldDummy.getScene().getWindow();
        }
        return null;
    }
}
