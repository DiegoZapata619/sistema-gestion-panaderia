package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

public class AdminController extends MenuController {

    private static final String REPO_URL = "https://github.com/DiegoZapata619/sistema-gestion-panaderia.git";

    @FXML
    Text NodeText;

    @FXML
    public void abrirRepositorio (ActionEvent event){
        openUrl(REPO_URL);
    }
    public void cerrarSesion(){
        openNewStage(LOGIN_VIEW,"Login");
        closeCurrentStage(NodeText);


    }

}
