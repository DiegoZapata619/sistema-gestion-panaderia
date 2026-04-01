package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class LoginController {
    private TextField Usuario;
    private TextField Password;


    @FXML
    private Button LoginButton;

    @FXML
    public void initialize() {
        System.out.println("Vista cargada correctamente");
    }
    public void inicioSesion (String usuario, String password){


    }
}