package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AdminController extends MenuController {

    private static final String REPO_URL = "https://github.com/DiegoZapata619/sistema-gestion-panaderia.git";

    //Nodo para poder obtener el stage actual. En el archivo fxml posee un identificador para ser enlazado
    @FXML
    Text NodeText;

    @FXML
    public void abrirRepositorio (ActionEvent event){
        openUrl(REPO_URL);
    }
    public void cerrarSesion(){
        Stage currentStage= (Stage) NodeText.getScene().getWindow();
        openNewStage(LOGIN_VIEW,titulosFxml.get(LOGIN_VIEW),null,null);
        currentStage.close();
    }
    public void abrirGestorInventario (){
        Stage currentStage= (Stage) NodeText.getScene().getWindow();
        openNewStage(INVENTORY_VIEW, titulosFxml.get(INVENTORY_VIEW),currentStage, ADMIN_VIEW);
    }

}
