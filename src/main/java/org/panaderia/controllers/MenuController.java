package org.panaderia.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
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
    public static final String ADMIN_VIEW = "/views/adminView.fxml";
    public static final String EMPLOYEE_VIEW = "/views/employeeView.fxml";
    public static final String INVENTORY_VIEW = "/views/inventoryView.fxml";
    public static final String PROMOTIONS_VIEW = "/views/promocionesView.fxml";
    static Alert defaultAlert;
    static ButtonType acceptButton = new ButtonType("Aceptar");
    public static HashMap<String, String> titulosFxml = new HashMap<>();

    //Constructor de MenuController, Contiene todas las escenas registradas
    public MenuController() {
        titulosFxml.put(LOGIN_VIEW, "Inicio de Sesión");
        titulosFxml.put(ADMIN_VIEW, "Vista de Administrador");
        titulosFxml.put(EMPLOYEE_VIEW, "Vista de Empleado");
        titulosFxml.put(INVENTORY_VIEW, "Inventario");
        titulosFxml.put(PROMOTIONS_VIEW, "Promociones");
    }

    //Funcion para poder cerrar la ventana actual
    //Mediante un nodo cualquiera de la escena para obtener el stage mediante getWindow()
    //posteriormente se cierra
    void closeCurrentStage(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    //Funcion principal para cambio de pestaña
    //Recibe el nombre del archivo fxml a abrir y el título de la ventana
    /*
    Agregago Final: Es mejor que cada stage se ajuste a su tamaño definido con
    stage.sizeToScene(). Ahora la función necesita que se le pasen como parámetros
    el título del padre, el stage padre y su título, de modo que se relacionen las stages
    para los eventos de retroceder al cerrar la ventana
     */
    public void openNewStage(String fxmlFileName, String title, Stage parentStage, String parentFxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuController.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            // Mantiene el tamaño dinámico.
            stage.sizeToScene();
            stage.setResizable(true);
            // Centrar ventana.
            stage.centerOnScreen();
            stage.setTitle(title);
            stage.setScene(scene);
            if (parentStage!=null) parentStage.hide();
            configureStageCloseEvent(stage,parentStage,parentFxml);
            stage.show();

        } catch (IOException | NullPointerException e) {
            setAlert(Alert.AlertType.WARNING, "Error cargando la vista: " + e.getMessage());
        }
    }

    //Configura el evento de cambio de pestaña.
    //Ahora se hace una relación entre pestaña padre e hija, permitiendo que cada que se abra una
    // ventana se mantenga el título.
    private void configureStageCloseEvent(Stage childStage, Stage parentStage, String parentFxml) {
        if (parentStage != null) {
            childStage.setOnCloseRequest(e -> {
                String tituloPadre = titulosFxml.getOrDefault(parentFxml, "Ventana");
                parentStage.setTitle(tituloPadre);
                parentStage.show();
                parentStage.toFront();
            });
        }
    }

    //Funcion para emitir alertas
    static public void setAlert(Alert.AlertType alertType, String argument) {
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

    //Metodo auxuliar para limpiar campos de texto
    static public void limpiarCeldas(TextField... cells) {
        for (TextField cell : cells) {
            cell.clear();
        }

    }

    static public boolean celdasVacias(TextField... cells) {
        for (TextField cell : cells) {
            if (cell==null || cell.getText().trim().isEmpty()){
                return true;
            }
        }
        return false;
    }
}




