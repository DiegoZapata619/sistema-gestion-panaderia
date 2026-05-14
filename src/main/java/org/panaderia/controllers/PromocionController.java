package org.panaderia.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.panaderia.DAO.PromocionDAO;
import org.panaderia.model.Descuentos.DescuentoFactory;
import org.panaderia.model.Descuentos.IEstrategiaDescuento;
import org.panaderia.model.Descuentos.TipoDescuento;
import org.panaderia.model.Promocion;

import java.io.IOException;
import java.util.List;

public class PromocionController extends MenuController {
    @FXML
    private TextField txtID;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtParam1;

    @FXML
    private TextField txtParam2;

    @FXML
    private ComboBox<TipoDescuento> combTipo;

    @FXML
    private ComboBox<String> combEstatus;

    @FXML
    private TableView<Promocion> tablaPromociones;

    @FXML
    private TableColumn<Promocion, String> colID;

    @FXML
    private TableColumn<Promocion, String> colNombre;

    @FXML
    private TableColumn<Promocion, String> colEstatus;

    @FXML
    private TableColumn<Promocion, String> colTipo;

    @FXML
    private TableColumn<Promocion, String> colParametro1;

    @FXML
    private TableColumn<Promocion, String> colParametro2;

    private final PromocionDAO promocionDAO = new PromocionDAO();

    private final ObservableList<Promocion> promociones = FXCollections.observableArrayList();

    private static final String PROMOCIONES_FILE =
            System.getProperty("user.dir") + "/data/promociones.csv";

    @FXML
    public void initialize() {
        configurarComboBoxes();
        configurarTabla();
        cargarPromociones();
    }

    private void configurarComboBoxes() {
        combEstatus.getItems().addAll("Activo", "Inactivo");
        combEstatus.setValue("Activo");

        combTipo.getItems().addAll(TipoDescuento.values());
    }

    private void configurarTabla() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstatus.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoTexto"));
        colParametro1.setCellValueFactory(new PropertyValueFactory<>("parametro1"));
        colParametro2.setCellValueFactory(new PropertyValueFactory<>("parametro2"));

        tablaPromociones.setItems(promociones);
    }

    private void cargarPromociones() {
        try {
            List<Promocion> lista = promocionDAO.leer(PROMOCIONES_FILE);
            promociones.setAll(lista);
        } catch (IOException e) {
            System.out.println("Error al cargar promociones: " + e.getMessage());
        }
    }

    private boolean obtenerEstadoSeleccionado() {
        return "Activo".equals(combEstatus.getValue());
    }


    public void agregarPromocion(){
        //ID,nombre,categoria,precio,stock,stockMinimo,descripcion
        if (celdasVacias(
                txtID, txtNombre, txtParam1, txtParam2)) {
            setAlert(Alert.AlertType.WARNING, "Alguno de los campos está vacío");
            return;
        }
        if (combTipo.getValue() == null) {
            setAlert(Alert.AlertType.WARNING, "Selecciona un tipo de descuento");
            return;
        }
        try{
            IEstrategiaDescuento estrategia = DescuentoFactory.crear(
                    combTipo.getValue(),txtParam1.getText(),txtParam2.getText());
            Promocion nuevo= new Promocion(
                    txtID.getText(),
                    txtNombre.getText(),estrategia
                    );
            nuevo.setActivo(obtenerEstadoSeleccionado());
            promocionDAO.agregar(PROMOCIONES_FILE,nuevo);
            cargarPromociones();
            limpiarCampos();
            setAlert(Alert.AlertType.INFORMATION, "Promocion agregada correctamente");

        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR,
                    "Ocurrió un error al guardar el producto");
        } catch (NumberFormatException e){
            setAlert(Alert.AlertType.ERROR,
                    "Los numericos deben ser válidos");
        }
    }

    public void eliminarPromocion(){
        Promocion seleccionado = tablaPromociones.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            setAlert(Alert.AlertType.WARNING,"No se ha seleccionado un producto");
            return;
        }

        try {
            boolean eliminada = promocionDAO.eliminar(
                    PROMOCIONES_FILE,
                    seleccionado.getId()
            );
            if (eliminada) {
                cargarPromociones();
                limpiarCampos();
                setAlert(Alert.AlertType.INFORMATION, "Promoción eliminada correctamente");
            } else {
                setAlert(Alert.AlertType.WARNING, "No se encontró la promoción");
            }

        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al eliminar");

        }
    }

    public void actualizarPromocion() {
        try {
            IEstrategiaDescuento estrategia= DescuentoFactory.crear(
                    combTipo.getValue(),txtParam1.getText(),txtParam2.getText());
            Promocion actualizado = new Promocion(
                    txtID.getText(),
                    txtNombre.getText(),
                    estrategia);
            actualizado.setActivo(obtenerEstadoSeleccionado());
            boolean actualizadoCorrectamente = promocionDAO.actualizar(
                    PROMOCIONES_FILE,
                    actualizado
            );

            if (actualizadoCorrectamente) {
                cargarPromociones();
                limpiarCampos();
                setAlert(Alert.AlertType.INFORMATION, "Promoción actualizada correctamente");
            } else {
                setAlert(Alert.AlertType.WARNING, "No se encontró la promoción a actualizar");
            }

        } catch (NumberFormatException e){
            setAlert(Alert.AlertType.ERROR, "Los valores numéricos deben ser válidos");
        }
        catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al actualizar"+e.getMessage());

        }
    }

    public void limpiarCampos (){
        limpiarCeldas(txtID, txtNombre, txtParam1, txtParam2);
        combTipo.setValue(null);
        combEstatus.setValue("Activo");
        tablaPromociones.getSelectionModel().clearSelection();
    }
    public void seleccionarFila(){
        Promocion prom= tablaPromociones.getSelectionModel().getSelectedItem();
        if (prom!=null){
            txtID.setText(prom.getId());
            txtNombre.setText(prom.getNombre());
            txtParam1.setText(prom.getParametro1());
            txtParam2.setText(prom.getParametro2());
            combTipo.setValue(TipoDescuento.valueOf(prom.getTipoTexto()));
            if (prom.isActivo()) {
                combEstatus.setValue("Activo");
            } else {
                combEstatus.setValue("Inactivo");
            }
        }
}
}

