package org.panaderia.controllers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.panaderia.DAO.ClienteDAO;
import org.panaderia.DAO.ProductDAO;
import org.panaderia.DAO.PromocionDAO;
import org.panaderia.Servicios.ItemCarrito;
import org.panaderia.Servicios.VentaServicio;
import org.panaderia.model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class GenerarVentaController extends MenuController implements Initializable {

    @FXML
    private ComboBox<Cliente> comboClientes;

    @FXML
    private ComboBox<Producto> comboProductos;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TableView<ItemCarrito> tablaCarrito;

    @FXML
    private TableColumn<ItemCarrito, String> colIdProducto;

    @FXML
    private TableColumn<ItemCarrito, String> colNombreProducto;

    @FXML
    private TableColumn<ItemCarrito, BigDecimal> colPrecioUnitario;

    @FXML
    private TableColumn<ItemCarrito, Integer> colCantidad;

    @FXML
    private TableColumn<ItemCarrito, BigDecimal> colSubtotal;

    @FXML
    private TableColumn<ItemCarrito, Void> colAcciones;

    @FXML
    private ComboBox<MetodoPago> comboMetodoPago;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblDescuento;

    @FXML
    private Label lblTotal;

    private ClienteDAO clienteDAO;
    private ProductDAO productDAO;
    private VentaServicio ventaServicio;

    private ObservableList<ItemCarrito> carrito;

    private List<Promocion> promocionesActivas;

    private Empleado empleadoActual = Sesion.getEmpleado();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clienteDAO = new ClienteDAO();
            productDAO = new ProductDAO();
            ventaServicio = new VentaServicio();
            carrito = FXCollections.observableArrayList();

            configurarTabla();
            configurarValidaciones();
            actualizarResumen();

            Platform.runLater(this::configurarCombos);

        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setEmpleadoActual(Empleado empleado) {
        if (empleado != null) {
            this.empleadoActual = empleado;
        }
    }

    private void configurarCombos() {
        try {
            String rutaClientes = System.getProperty("user.dir") + "/data/clientes.csv";
            List<Cliente> clientes = clienteDAO.leer(rutaClientes);

            comboClientes.setItems(FXCollections.observableArrayList(clientes));

            comboClientes.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Cliente cliente, boolean empty) {
                    super.updateItem(cliente, empty);

                    if (empty || cliente == null) {
                        setText(null);
                    } else {
                        setText(cliente.getNombre() + " (" + cliente.getId() + ")");
                    }
                }
            });

            comboClientes.setButtonCell(comboClientes.getCellFactory().call(null));

            comboClientes.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, oldVal, newVal) -> actualizarResumen());

            List<Producto> productos = productDAO.obtenerTodos();

            comboProductos.setItems(FXCollections.observableArrayList(productos));

            comboProductos.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Producto producto, boolean empty) {
                    super.updateItem(producto, empty);

                    if (empty || producto == null) {
                        setText(null);
                    } else {
                        setText(
                                producto.getNombre()
                                        + " - $"
                                        + String.format("%.2f", producto.getPrecio())
                                        + " (Stock: "
                                        + producto.getStock()
                                        + ")"
                        );
                    }
                }
            });

            comboProductos.setButtonCell(comboProductos.getCellFactory().call(null));

            comboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));

            PromocionDAO promocionDAO = new PromocionDAO();
            String rutaPromociones = System.getProperty("user.dir") + "/data/promociones.csv";

            List<Promocion> todasLasPromociones = promocionDAO.leer(rutaPromociones);

            promocionesActivas = todasLasPromociones.stream()
                    .filter(Promocion::isActivo)
                    .toList();

            actualizarResumen();

        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error al cargar datos: " + e.getMessage());
        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error inesperado al cargar datos: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        colPrecioUnitario.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + value.setScale(2, RoundingMode.HALF_UP));
                }
            }
        });

        colSubtotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + value.setScale(2, RoundingMode.HALF_UP));
                }
            }
        });

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
                btnEliminar.setOnAction(event -> {
                    ItemCarrito item = getTableView().getItems().get(getIndex());
                    eliminarItem(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });

        tablaCarrito.setItems(carrito);
    }

    private void configurarValidaciones() {
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtCantidad.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        txtCantidad.setText("1");
    }

    @FXML
    public void agregarProducto() {

        /**
         * Antes de cada venta, se realizan revisiones entre la cantidad solicitada de cada
         * producto y el stock disponible. Asimismo se verifican campos vacios
         */

        /// Chequeos de datos vacios
        if (comboClientes.getSelectionModel().getSelectedItem() == null) {
            setAlert(Alert.AlertType.WARNING, "Debe seleccionar un cliente antes de agregar productos.");
            return;
        }

        if (comboProductos.getSelectionModel().getSelectedItem() == null) {
            setAlert(Alert.AlertType.WARNING, "Debe seleccionar un producto.");
            return;
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            /// Cantidad invalida de producto. No se aceptan menos o iguales a 0
            if (cantidad <= 0) {
                setAlert(Alert.AlertType.WARNING, "La cantidad debe ser mayor a 0.");
                return;
            }

        } catch (NumberFormatException e) {
            setAlert(Alert.AlertType.WARNING, "Ingrese una cantidad válida.");
            return;
        }

        Producto producto = comboProductos.getSelectionModel().getSelectedItem();

        /// Cantidad supera al stock
        if (cantidad > producto.getStock()) {
            setAlert(
                    Alert.AlertType.WARNING,
                    "Stock insuficiente. Disponible: " + producto.getStock()
            );
            return;
        }
        /// Cantidad deja a las existencias del producto menores o iguales a su stock minimo
        if ((cantidad - producto.getStock()) <= producto.getStockMinimo()) {
            setAlert(Alert.AlertType.WARNING, "La orden sobrepasa el stock mínimo." +
                    " Disponible: " + producto.getStock() + " Minimo: " + producto.getStockMinimo());
        }

        ItemCarrito existente = buscarItemEnCarrito(producto.getId());
        /// Si se sobrepasa el stock actual
        if (existente != null) {
            int total = existente.getCantidad() + cantidad;

            if (total > producto.getStock()) {
                setAlert(
                        Alert.AlertType.WARNING,
                        "Stock insuficiente. Total en carrito sería: "
                                + total
                                + ", disponible: "
                                + producto.getStock()
                );
                return;
            }

            existente.setCantidad(total);

        } else {
            carrito.add(new ItemCarrito(producto, cantidad));
        }

        tablaCarrito.refresh();
        actualizarResumen();
        limpiarSeleccionProducto();
    }

    private void eliminarItem(ItemCarrito item) {
        carrito.remove(item);
        tablaCarrito.refresh();
        actualizarResumen();
    }

    @FXML
    public void vaciarCarrito() {
        carrito.clear();
        tablaCarrito.refresh();
        actualizarResumen();
    }

    private void actualizarResumen() {
        if (carrito == null || lblSubtotal == null || lblDescuento == null || lblTotal == null) {
            return;
        }

        BigDecimal subtotal = carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal descuento = BigDecimal.ZERO;

        Cliente clienteSeleccionado = null;

        if (comboClientes != null) {
            clienteSeleccionado = comboClientes.getSelectionModel().getSelectedItem();
        }

        if (
                clienteSeleccionado != null
                        && empleadoActual != null
                        && !carrito.isEmpty()
                        && promocionesActivas != null
                        && !promocionesActivas.isEmpty()
        ) {
            Venta ventaTemporal = new Venta(
                    "TEMP",
                    clienteSeleccionado,
                    empleadoActual,
                    MetodoPago.EFECTIVO
            );

            for (ItemCarrito item : carrito) {
                ventaTemporal.agregarDetalle(item.getProductoRef(), item.getCantidad());
            }

            ventaTemporal.aplicarPromociones(promocionesActivas);
            descuento = ventaTemporal.getDescuentoAplicado();
        }

        BigDecimal total = subtotal.subtract(descuento).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblDescuento.setText(String.format("-$%.2f", descuento));
        lblTotal.setText(String.format("$%.2f", total));
    }

    @FXML
    public void confirmarVenta() {
        try {
            if (empleadoActual == null) {
                setAlert(Alert.AlertType.ERROR, "No hay empleado activo en la sesión.");
                return;
            }

            if (comboClientes.getSelectionModel().getSelectedItem() == null) {
                setAlert(Alert.AlertType.WARNING, "Debe seleccionar un cliente.");
                return;
            }

            if (carrito.isEmpty()) {
                setAlert(Alert.AlertType.WARNING, "El carrito está vacío.");
                return;
            }

            if (comboMetodoPago.getSelectionModel().getSelectedItem() == null) {
                setAlert(Alert.AlertType.WARNING, "Debe seleccionar un método de pago.");
                return;
            }

            Cliente cliente = comboClientes.getSelectionModel().getSelectedItem();
            MetodoPago metodo = comboMetodoPago.getSelectionModel().getSelectedItem();

            String idVenta = "V" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            Venta venta = new Venta(idVenta, cliente, empleadoActual, metodo);

            for (ItemCarrito item : carrito) {
                venta.agregarDetalle(item.getProductoRef(), item.getCantidad());
            }

            if (promocionesActivas != null && !promocionesActivas.isEmpty()) {
                venta.aplicarPromociones(promocionesActivas);
            }

            ventaServicio.registrarVenta(venta);

            mostrarComprobante(venta);

            vaciarCarrito();
            comboClientes.getSelectionModel().clearSelection();
            comboMetodoPago.getSelectionModel().clearSelection();

            setAlert(Alert.AlertType.INFORMATION, "Venta registrada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            setAlert(Alert.AlertType.ERROR, "Error al confirmar venta: " + e.getMessage());
        }
    }

    private ItemCarrito buscarItemEnCarrito(String idProducto) {
        return carrito.stream()
                .filter(item -> item.getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
    }

    private void limpiarSeleccionProducto() {
        comboProductos.getSelectionModel().clearSelection();
        txtCantidad.setText("1");
    }

    private void mostrarComprobante(Venta venta) {
        /// Necesario cargar el comprobante con loader y stage para poder pasar Venta al comprobante
        /// De esta manera, antes de iniciar el stage ya se setea la venta como parte del comprovante,
        /// de donde el ComprobanteController obtiene los datos
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(COMPROBANTE));
            Parent root = loader.load();
            ComprobanteController comprobanteController = loader.getController();
            comprobanteController.setVenta(venta);
            Stage stage = new Stage();
            stage.setTitle(titulosFxml.get(COMPROBANTE));
            stage.setScene(new Scene(root));
            stage.initOwner(getCurrentStage());
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            setAlert(Alert.AlertType.ERROR, "Error al mostrar comprobante: " + e.getMessage());
        }
    }

    @FXML
    public void cancelarVenta() {
        vaciarCarrito();
        comboClientes.getSelectionModel().clearSelection();
        comboMetodoPago.getSelectionModel().clearSelection();
        limpiarSeleccionProducto();
    }

    private Stage getCurrentStage() {
        if (tablaCarrito != null && tablaCarrito.getScene() != null) {
            return (Stage) tablaCarrito.getScene().getWindow();
        }

        return null;
    }
}

