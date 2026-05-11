package org.panaderia.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.panaderia.DAO.ProductDAO;
import org.panaderia.model.Producto;

import java.io.IOException;

public class InventoryController extends MenuController {
    @FXML
    private TableView<Producto> tablaProductos;

    @FXML private TableColumn<Producto, String> colID;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Integer> colStockMinimo;
    @FXML private TableColumn<Producto, String> colDescripcion;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtDescripcion;

    private static final String RUTA= "Productos.csv";
    private final ProductDAO productDAO= new ProductDAO();


    /*
    Metodo ejecutado por JavaFX al momento de cargar la vista del archivo fxml
    En este caso, initialize sirve para enlazar cada columna de la tabla con
    un atributo de los objetos a representar
    Las strings pasadas como parametro de PropertyValueFactory representan
    los atributos de la clase producto. Con el nombre de estos atributos se
    buscan los getters correspondientes,
     */
    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        cargarTabla();
    }
    @FXML
    private void cargarTabla (){
        try {
            tablaProductos.setItems(FXCollections.
                    observableArrayList(productDAO.leer(RUTA)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void agregarProducto (){
        //ID,nombre,categoria,precio,stock,stockMinimo,descripcion
            if (celdasVacias(
                    txtId, txtNombre, txtCategoria,
                    txtPrecio, txtStock, txtStockMinimo, txtDescripcion)) {
                setAlert(Alert.AlertType.WARNING, "Alguno de los campos está vacío");
                return;
            }
            if (Integer.parseInt(txtStock.getText())<=Integer.parseInt(txtStockMinimo.getText())) {
                setAlert(Alert.AlertType.ERROR, "El stock mínimo debe ser menor al stock");
            }
           try{
                Producto nuevo= new Producto(
                        txtId.getText(),
                        txtNombre.getText(),
                        txtCategoria.getText(),
                        Double.parseDouble(txtPrecio.getText()),
                        Integer.parseInt(txtStock.getText()),
                        Integer.parseInt(txtStockMinimo.getText()),
                        txtDescripcion.getText());

                productDAO.agregar(RUTA,nuevo);
                cargarTabla();
                limpiarCampos();
               setAlert(Alert.AlertType.INFORMATION, "Producto agregado correctamente");

        } catch (IOException e) {
               setAlert(Alert.AlertType.ERROR,
                       "Ocurrió un error al guardar el producto");
        } catch (NumberFormatException e){
               setAlert(Alert.AlertType.ERROR,
                       "El stock y precio deben ser valores válidos");
           }
    }

    @FXML
    public void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null){
            setAlert(Alert.AlertType.WARNING,"No se ha seleccionado un producto");
            return;
        }

        if (Integer.parseInt(txtStock.getText())<=Integer.parseInt(txtStockMinimo.getText())) {
            setAlert(Alert.AlertType.ERROR, "El stock mínimo debe ser menor al stock");
        }

        try {

            boolean eliminado= productDAO.eliminar(RUTA,seleccionado.getId());
            if (eliminado){
                cargarTabla();
                limpiarCeldas();
                setAlert(Alert.AlertType.INFORMATION, "Producto eliminado correctamente");

            }
            else {
                setAlert(Alert.AlertType.WARNING, "No se encontró el producto");
            }


        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al eliminar");

        }
    }
    @FXML
    public void actualizarProducto() {
        try {
            Producto actualizado = new Producto(
                    txtId.getText(),
                    txtNombre.getText(),
                    txtCategoria.getText(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText()),
                    Integer.parseInt(txtStockMinimo.getText()),
                    txtDescripcion.getText()
            );

            productDAO.actualizar(RUTA, actualizado);
            cargarTabla();

        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al actualizar");
        }
    }

    public void seleccionarFila(){
        Producto p= tablaProductos.getSelectionModel().getSelectedItem();
        if (p!=null){
            txtId.setText(p.getId());
            txtNombre.setText(p.getNombre());
            txtCategoria.setText(p.getCategoria());
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtStock.setText(String.valueOf(p.getStock()));
            txtStockMinimo.setText(String.valueOf(p.getStockMinimo()));
            txtDescripcion.setText(p.getDescripcion());

        }
    }

    @FXML
    public void limpiarCampos(){
        limpiarCeldas(txtId,txtNombre,txtCategoria,txtPrecio,txtStock,txtStockMinimo,txtDescripcion);

    }

}
