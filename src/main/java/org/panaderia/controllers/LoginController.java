package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.panaderia.Servicios.Autenticador;
import org.panaderia.model.Rol;
import org.panaderia.model.Usuario;

import java.io.IOException;

    /*
    Credenciales para acceder al sistema, están en formato usuario : contraseña
        admin : testing1
        user : testing2
    */
public class LoginController extends MenuController{
    @FXML
    private TextField TxtUsuario;
    @FXML
    private PasswordField TxtPassword;

    @FXML
    private Label lblMensaje;

    private final Autenticador autenticador= new Autenticador();

    @FXML
    private void inicioSesion() {
        //uso de trim() para remover espacios en blanco que afecten al getText()
        String nombre= TxtUsuario.getText().trim();
        String password= TxtPassword.getText().trim();

        try {
            Usuario usuario= autenticador.Autenticar(nombre,password);
            if (usuario==null){
                lblMensaje.setText("Usuario/Contraseña incorrectos");
                return;
            }
            //Dependiendo del rol se carga una vista distinta
            if (usuario.getRol()== Rol.ADMINISTRADOR){
                openNewStage(ADMIN_VIEW,"Menú Principal");
                closeCurrentStage(TxtUsuario);

            } else if (usuario.getRol()==Rol.EMPLEADO) {
                openNewStage(EMPLOYEE_VIEW,"Menú Principal");
                closeCurrentStage(TxtUsuario);
            }

        } catch (IOException e){
            lblMensaje.setText("Error al ler usuario");
            e.printStackTrace();

        }
    }
}