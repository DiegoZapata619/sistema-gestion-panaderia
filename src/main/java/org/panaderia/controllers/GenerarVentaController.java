package org.panaderia.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.panaderia.DAO.ClienteDAO;
import org.panaderia.DAO.ProductDAO;
import org.panaderia.DAO.PromocionDAO;
import org.panaderia.Servicios.VentaServicio;
import org.panaderia.model.*;
import org.panaderia.utils.SesionActual;

import java.io.IOException;
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
    private TableColumn<ItemCarrito, Double> colPrecioUnitario;
    
    @FXML
    private TableColumn<ItemCarrito, Integer> colCantidad;
    
    @FXML
    private TableColumn<ItemCarrito, Double> colSubtotal;
    
    @FXML
    private TableColumn<ItemCarrito, Void> colAcciones;
    
    @FXML
    private ComboBox<MetodoPago> comboMetodoPago;
    
    @FXML
    private ComboBox<Promocion> comboDescuento;
    
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
            
            // Cargar combos de forma asíncrona para no bloquear la carga
            javafx.application.Platform.runLater(this::configurarCombos);
            
        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void configurarCombos() {
        try {
            // Cargar clientes
            String rutaClientes = System.getProperty("user.dir") + "/data/clientes.csv";
            List<Cliente> clientes = clienteDAO.leer(rutaClientes);
            comboClientes.setItems(FXCollections.observableArrayList(clientes));
            
            // Personalizar display de clientes
            comboClientes.setCellFactory(param -> new ListCell<Cliente>() {
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
            
            // Cargar productos
            List<Producto> productos = productDAO.obtenerTodos();
            comboProductos.setItems(FXCollections.observableArrayList(productos));
            
            // Personalizar display de productos
            comboProductos.setCellFactory(param -> new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto producto, boolean empty) {
                    super.updateItem(producto, empty);
                    if (empty || producto == null) {
                        setText(null);
                    } else {
                        setText(producto.getNombre() + " - $" + String.format("%.2f", producto.getPrecio()) + " (Stock: " + producto.getStock() + ")");
                    }
                }
            });
            
            // Cargar métodos de pago
            comboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
            
            // Cargar descuentos
            try {
                PromocionDAO promocionDAO = new PromocionDAO();
                String rutaPromociones = System.getProperty("user.dir") + "/data/promociones.csv";
                System.out.println("Cargando promociones desde: " + rutaPromociones);
                
                List<Promocion> promociones = promocionDAO.leer(rutaPromociones);
                System.out.println("Promociones cargadas: " + promociones.size());
                
                comboDescuento.setItems(FXCollections.observableArrayList(promociones));
                
                // Añadir opción "Sin descuento"
                comboDescuento.getItems().add(0, null); // Opción nula para sin descuento
            } catch (Exception e) {
                System.err.println("Error al cargar promociones: " + e.getMessage());
                e.printStackTrace();
                // Añadir solo opción sin descuento si hay error
                comboDescuento.setItems(FXCollections.observableArrayList());
            }
            
            // Personalizar display de descuentos
            comboDescuento.setCellFactory(param -> new ListCell<Promocion>() {
                @Override
                protected void updateItem(Promocion promocion, boolean empty) {
                    super.updateItem(promocion, empty);
                    if (empty || promocion == null) {
                        setText(null);
                    } else {
                        String nombre = promocion.getNombre();
                        String descripcion = promocion.getDescripcionEstrategia();
                        setText(nombre + " - " + descripcion);
                    }
                }
            });
            
            // Añadir listener para actualizar totales cuando cambie el descuento
            comboDescuento.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                actualizarResumen();
            });
            
        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void configurarTabla() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        
        // Configurar columna de acciones (botón eliminar)
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
        
        tablaCarrito.setItems(carrito);
    }
    
    private void configurarValidaciones() {
        // Validar que la cantidad sea un número positivo
        txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    @FXML
    public void agregarProducto() {
        try {
            // Validaciones
            if (comboClientes.getSelectionModel().getSelectedItem() == null) {
                setAlert(Alert.AlertType.WARNING, "Debe seleccionar un cliente");
                return;
            }
            
            if (comboProductos.getSelectionModel().getSelectedItem() == null) {
                setAlert(Alert.AlertType.WARNING, "Debe seleccionar un producto");
                return;
            }
            
            int cantidad;
            try {
                cantidad = Integer.parseInt(txtCantidad.getText());
                if (cantidad <= 0) {
                    setAlert(Alert.AlertType.WARNING, "La cantidad debe ser mayor a 0");
                    return;
                }
            } catch (NumberFormatException e) {
                setAlert(Alert.AlertType.WARNING, "Ingrese una cantidad válida");
                return;
            }
            
            Producto producto = comboProductos.getSelectionModel().getSelectedItem();
            
            // Validar stock
            if (cantidad > producto.getStock()) {
                setAlert(Alert.AlertType.WARNING, "Stock insuficiente. Stock disponible: " + producto.getStock());
                return;
            }
            
            // Verificar si el producto ya está en el carrito
            ItemCarrito itemExistente = buscarItemEnCarrito(producto.getId());
            if (itemExistente != null) {
                int nuevaCantidad = itemExistente.getCantidad() + cantidad;
                if (nuevaCantidad > producto.getStock()) {
                    setAlert(Alert.AlertType.WARNING, "Stock insuficiente. Total en carrito sería: " + nuevaCantidad + ", Stock disponible: " + producto.getStock());
                    return;
                }
                itemExistente.setCantidad(nuevaCantidad);
                itemExistente.actualizarSubtotal();
            } else {
                ItemCarrito nuevoItem = new ItemCarrito(producto, cantidad);
                carrito.add(nuevoItem);
            }
            
            tablaCarrito.refresh();
            actualizarResumen();
            limpiarSeleccionProducto();
            
        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al agregar producto: " + e.getMessage());
        }
    }
    
    private ItemCarrito buscarItemEnCarrito(String idProducto) {
        return carrito.stream()
                .filter(item -> item.getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
    }
    
    private void eliminarItem(ItemCarrito item) {
        carrito.remove(item);
        tablaCarrito.refresh();
        actualizarResumen();
    }
    
    private void actualizarResumen() {
        double subtotal = carrito.stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
        
        // Calcular descuento si hay una promoción seleccionada
        double descuento = 0.0;
        Promocion promocionSeleccionada = comboDescuento.getSelectionModel().getSelectedItem();
        if (promocionSeleccionada != null && !carrito.isEmpty()) {
            // Crear una venta temporal para calcular el descuento
            Venta ventaTemporal = new Venta("TEMP", 
                comboClientes.getSelectionModel().getSelectedItem(), 
                new Empleado("Temp", Rol.EMPLEADO), 
                MetodoPago.EFECTIVO);
            
            // Agregar productos del carrito
            for (ItemCarrito item : carrito) {
                try {
                    Producto producto = productDAO.buscarPorId(item.getIdProducto()).orElse(null);
                    if (producto != null) {
                        ventaTemporal.agregarDetalle(producto, item.getCantidad());
                    }
                } catch (Exception e) {
                    System.err.println("Error al buscar producto: " + e.getMessage());
                }
            }
            
            descuento = promocionSeleccionada.aplicarDescuento(ventaTemporal).doubleValue();
        }
        
        double total = subtotal - descuento;
        
        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblDescuento.setText(String.format("$%.2f", descuento));
        lblTotal.setText(String.format("$%.2f", total));
    }
    
    private void limpiarSeleccionProducto() {
        comboProductos.getSelectionModel().clearSelection();
        txtCantidad.setText("1");
    }
    
    @FXML
    public void vaciarCarrito() {
        carrito.clear();
        tablaCarrito.refresh();
        actualizarResumen();
    }
    
    @FXML
    public void confirmarVenta() {
        try {
            // Validaciones
            if (comboClientes.getSelectionModel().getSelectedItem() == null) {
                setAlert(Alert.AlertType.WARNING, "Debe seleccionar un cliente");
                return;
            }
            
            if (carrito.isEmpty()) {
                setAlert(Alert.AlertType.WARNING, "El carrito está vacío");
                return;
            }
            
            if (comboMetodoPago.getSelectionModel().getSelectedItem() == null) {
                setAlert(Alert.AlertType.WARNING, "Debe seleccionar un método de pago");
                return;
            }
            
            // Crear objeto Venta
            Cliente cliente = comboClientes.getSelectionModel().getSelectedItem();
            MetodoPago metodoPago = comboMetodoPago.getSelectionModel().getSelectedItem();
            
            // Obtener el usuario actual de la sesión
            Usuario usuarioActual = SesionActual.getUsuarioActual();
            Empleado empleado;
            
            if (usuarioActual != null) {
                empleado = new Empleado(usuarioActual.getNombre(), usuarioActual.getRol());
            } else {
                // Fallback si no hay sesión activa
                empleado = new Empleado("Empleado Actual", Rol.EMPLEADO);
            }
            
            String idVenta = "V" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            Venta venta = new Venta(idVenta, cliente, empleado, metodoPago);
            
            // Agregar detalles a la venta
            for (ItemCarrito item : carrito) {
                Producto producto = productDAO.buscarPorId(item.getIdProducto())
                        .orElseThrow(() -> new IOException("Producto no encontrado: " + item.getIdProducto()));
                venta.agregarDetalle(producto, item.getCantidad());
            }
            
            // Aplicar descuento si hay uno seleccionado
            Promocion promocionSeleccionada = comboDescuento.getSelectionModel().getSelectedItem();
            if (promocionSeleccionada != null) {
                double descuento = promocionSeleccionada.aplicarDescuento(venta).doubleValue();
                venta.setDescuentoAplicado(java.math.BigDecimal.valueOf(descuento));
                System.out.println("Descuento aplicado: $" + descuento);
            }
            
            // Registrar venta
            ventaServicio.registrarVenta(venta);
            
            // Mostrar comprobante
            mostrarComprobante(venta);
            
            // Limpiar y cerrar
            vaciarCarrito();
            comboClientes.getSelectionModel().clearSelection();
            comboMetodoPago.getSelectionModel().clearSelection();
            
            setAlert(Alert.AlertType.INFORMATION, "Venta registrada exitosamente");
            
        } catch (IOException e) {
            setAlert(Alert.AlertType.ERROR, "Error de archivo al registrar venta: " + e.getMessage());
            System.err.println("Error al guardar venta: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            setAlert(Alert.AlertType.ERROR, "Error al confirmar venta: " + e.getMessage());
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarComprobante(Venta venta) {
        try {
            openNewStage(COMPROBANTE, titulosFxml.get(COMPROBANTE), getCurrentStage(), null);
            // Aquí deberíamos pasar los datos de la venta al controlador del comprobante
        } catch (Exception e) {
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
        if (tablaCarrito != null) {
            return (Stage) tablaCarrito.getScene().getWindow();
        }
        return null;
    }
    
    // Clase interna para representar items del carrito
    public static class ItemCarrito {
        private String idProducto;
        private String nombreProducto;
        private double precioUnitario;
        private int cantidad;
        private double subtotal;
        
        public ItemCarrito(Producto producto, int cantidad) {
            this.idProducto = producto.getId();
            this.nombreProducto = producto.getNombre();
            this.precioUnitario = producto.getPrecio();
            this.cantidad = cantidad;
            actualizarSubtotal();
        }
        
        public void actualizarSubtotal() {
            this.subtotal = precioUnitario * cantidad;
        }
        
        // Getters
        public String getIdProducto() { return idProducto; }
        public String getNombreProducto() { return nombreProducto; }
        public double getPrecioUnitario() { return precioUnitario; }
        public int getCantidad() { return cantidad; }
        public double getSubtotal() { return subtotal; }
        public void setCantidad(int cantidad) { 
            this.cantidad = cantidad;
            actualizarSubtotal();
        }
    }
}
