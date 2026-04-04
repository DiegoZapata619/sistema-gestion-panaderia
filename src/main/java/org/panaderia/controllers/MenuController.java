package org.panaderia.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.HashMap;

//Clase padre que todos los controllers extienden
//Contiene métodos esenciales para cambio de pestañas, cierre de pestañas y otros servicios
public class MenuController {
    public static final String LOGIN_VIEW = "/views/LoginView.fxml";
    public static final String ADMIN_VIEW= "/views/adminView.fxml";
    public static final String EMPLOYEE_VIEW = "/views/employeeView.fxml";
    static Alert defaultAlert;
    static ButtonType acceptButton = new ButtonType("Aceptar");
    public static HashMap<String, String > rutaArchivos = new HashMap<>();
    public static HashMap<String, String> titulosFxml = new HashMap<>();
    //Constructor de MenuController, Contiene todas las escenas registradas
    public MenuController (){
        titulosFxml.put(LOGIN_VIEW,"Inicio de Sesión");
        titulosFxml.put(ADMIN_VIEW,"Vista de Administrador");
        titulosFxml.put(EMPLOYEE_VIEW,"Vista de Empleado");
        rutaArchivos.put(ADMIN_VIEW, LOGIN_VIEW);
        rutaArchivos.put(EMPLOYEE_VIEW, LOGIN_VIEW);
    }
    //Funcion para poder cerrar la ventana actual
    //Mediante un nodo cualquiera de la escena para obtener el stage mediante getWindow()
    //posteriormente se cierra
    void closeCurrentStage(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
    //Obtienes el archivo fxml padre de la escena
    String getFxmlFather(String fxml){
        return rutaArchivos.get(fxml);
    }

    //Funcion principal para cambio de pestaña
    //Recibe el nombre del archivo fxml a abrir y el título de la ventana
    public void openNewStage(String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuController.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            // Mantiene el tamaño dinámico. Como se busca que Login sea un poco más alargado
            //se mantiene como un caso especial.
            if (fxmlFileName.equals(LOGIN_VIEW)){
                stage.setMinHeight(500);
                stage.setMinWidth(400);
            } else{
                stage.setMinHeight(480);
                stage.setMinWidth(600);
            }
            stage.setResizable(false);
            // Centrar ventana.
            stage.centerOnScreen();
            stage.setTitle(title);
            stage.setScene(scene);
            configureStageCloseEvent(stage, fxmlFileName);
            stage.show();

        } catch (IOException | NullPointerException e) {
            setAlert(Alert.AlertType.WARNING, "Error cargando la vista: "+ e.getMessage());
        }
    }

    //Configura el evento de cambio de pestaña.
    //Ahora se hace una relación entre pestaña padre e hija, permitiendo que cada que se abra una ventana se mantenga el título.
    private void configureStageCloseEvent(Stage stage, String fxmlFileName) {
        if (!fxmlFileName.equals(LOGIN_VIEW)) {
            stage.setOnCloseRequest(e -> {
                String padreFxml = getFxmlFather(fxmlFileName);
                String tituloPadre = titulosFxml.getOrDefault(padreFxml, "Ventana");
                openNewStage(padreFxml, tituloPadre);
            });
        }
    }
    //Funcion para emitir alertas
    static public void setAlert(Alert.AlertType alertType,String argument){
        defaultAlert = new Alert(alertType);
        defaultAlert.setTitle("Información");
        defaultAlert.setHeaderText(null);
        defaultAlert.getButtonTypes().setAll(acceptButton);
        defaultAlert.setContentText(argument);
        defaultAlert.showAndWait();
    }

    //Funcion para apertura de código URL. Usada por el MainController para acceder al repositorio
        public void openUrl(String url) {
            new Thread(() -> {
                try {
                    String os = System.getProperty("os.name").toLowerCase();

                    if (os.contains("win")) {
                        Runtime.getRuntime().exec(
                                new String[]{"rundll32", "url.dll,FileProtocolHandler", url}
                        );
                    } else if (os.contains("nux") || os.contains("nix") || os.contains("aix")) {
                        Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                    } else if (os.contains("mac")) {
                        Runtime.getRuntime().exec(new String[]{"open", url});
                    } else {
                        System.out.println("Sistema operativo no soportado.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


