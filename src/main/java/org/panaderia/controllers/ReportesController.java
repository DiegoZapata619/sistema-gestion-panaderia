package org.panaderia.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.panaderia.Servicios.VentaServicio;
import org.panaderia.model.Venta;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador JavaFX para la vista de reportes.
 * Gestiona tres tabs:
 *   1. Historial de ventas — con filtros por fecha, cliente y producto
 *   2. Reportes por período — diario, semanal, mensual, anual o personalizado
 *   3. Resumen por producto — cantidad vendida e ingresos por producto en un rango de fechas
 *
 * Delega toda la lógica de negocio a VentaServicio.
 */
public class ReportesController extends MenuController {

    @FXML private TabPane tabPaneReportes;
    @FXML private Tab tabHistorialVentas;
    @FXML private Tab tabReportesPeriodo;
    @FXML private Tab tabResumenProductos;

    // ── Tab 1: Historial de ventas ────────────────────────────────────────────

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String>     colIdVenta;
    @FXML private TableColumn<Venta, String>     colFecha;
    @FXML private TableColumn<Venta, String>     colCliente;
    @FXML private TableColumn<Venta, String>     colEmpleado;
    @FXML private TableColumn<Venta, BigDecimal> colTotal;

    @FXML private DatePicker datePickerFiltro;
    @FXML private TextField  txtFiltroCliente;
    @FXML private TextField  txtFiltroProducto;
    @FXML private Button     btnFiltrar;
    @FXML private Button     btnLimpiarFiltros;

    // ── Tab 2: Reportes por período ───────────────────────────────────────────

    @FXML private ComboBox<String> comboTipoReporte;
    @FXML private DatePicker       datePickerInicio;
    @FXML private DatePicker       datePickerFin;
    @FXML private Button           btnGenerarReporte;
    @FXML private TextArea         txtAreaReporte;

    // ── Tab 3: Resumen por producto ───────────────────────────────────────────

    @FXML private TableView<ResumenProductoTabla>       tablaResumenProductos;
    @FXML private TableColumn<ResumenProductoTabla, String>     colNombreProducto;
    @FXML private TableColumn<ResumenProductoTabla, String>     colCategoria;
    @FXML private TableColumn<ResumenProductoTabla, Integer>    colCantidad;
    @FXML private TableColumn<ResumenProductoTabla, BigDecimal> colTotalVentas;

    @FXML private DatePicker datePickerResumenInicio;
    @FXML private DatePicker datePickerResumenFin;
    @FXML private Button     btnGenerarResumen;
    @FXML private Label      lblTotalIngresos;

    private final VentaServicio ventaServicio = new VentaServicio();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Inicialización ────────────────────────────────────────────────────────

    /**
     * JavaFX llama a este método automáticamente después de cargar el FXML.
     * Configura las columnas de las tablas, puebla el combo de tipos de reporte
     * y carga el historial completo de ventas.
     */
    @FXML
    public void initialize() {
        configurarTablaVentas();
        configurarTablaResumen();
        configurarCombos();
        cargarDatosIniciales();
    }

    /**
     * Enlaza cada columna de la tabla de ventas con el getter correspondiente de Venta.
     *
     * CORRECCIÓN: las columnas anteriores usaban "fechaFormateada", "nombreCliente",
     * "nombreEmpleado" y "total" — getters que no existen en Venta. Se reemplazaron
     * por CellValueFactory con lambdas que acceden a los getters reales:
     *   getFecha(), getCliente().getNombre(), getEmpleado().getNombre(), calcularTotal().
     *
     * PropertyValueFactory solo funciona si el nombre coincide exactamente con un getter
     * (ej: "idVenta" → getIdVenta()). Para datos derivados o anidados, se usan lambdas.
     */
    private void configurarTablaVentas() {
        // "idVenta" → getIdVenta() — getter directo, PropertyValueFactory es suficiente
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));

        // Fecha formateada — getFecha() devuelve Date, se convierte a String con el formatter
        colFecha.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFecha().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            return new javafx.beans.property.SimpleStringProperty(
                    fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });

        // Nombre del cliente — dato anidado: getCliente().getNombre()
        colCliente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCliente().getNombre()));

        // Nombre del empleado — dato anidado: getEmpleado().getNombre()
        colEmpleado.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEmpleado().getNombre()));

        // Total calculado — no es un campo almacenado sino un metodo de cálculo
        colTotal.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().calcularTotal()));

        // Formato visual de la columna de total: agrega el símbolo "$"
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : "$" + item.toPlainString());
            }
        });
    }

    /**
     * Enlaza las columnas de la tabla de resumen con los getters de ResumenProductoTabla.
     * Estos sí usan PropertyValueFactory porque los nombres coinciden con getters directos.
     */
    private void configurarTablaResumen() {
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotalVentas.setCellValueFactory(new PropertyValueFactory<>("total"));

        colTotalVentas.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : "$" + item.toPlainString());
            }
        });
    }

    /**
     * Configura la combo box de tipo de reporte para mostrar las opciones disponibles
     * selecciona "Diario" por defecto.
     */
    private void configurarCombos() {
        comboTipoReporte.getItems().addAll("Diario", "Semanal", "Mensual", "Anual", "Personalizado");
        comboTipoReporte.getSelectionModel().selectFirst();
    }

    /**
     * Carga todas las ventas sin filtro al arrancar la vista.
     */
    private void cargarDatosIniciales() {
        try {
            List<Venta> ventas = ventaServicio.obtenerHistorialVentas();
            tablaVentas.getItems().setAll(ventas);
        } catch (IOException e) {
            mostrarAlerta("Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ── Tab 1: Filtrado ───────────────────────────────────────────────────────

    /**
     * Aplica los filtros seleccionados de forma acumulativa sobre la lista completa.
     *
     * CORRECCIÓN: la versión anterior aplicaba cada filtro sobre el resultado
     * del servicio de forma independiente, de modo que el último filtro activo
     * sobreescribía a los anteriores. Ahora todos los filtros se aplican
     * encadenados sobre la misma lista.
     */
    @FXML
    private void filtrarVentas() {
        try {
            LocalDate fechaFiltro    = datePickerFiltro.getValue();
            String    filtroCliente  = txtFiltroCliente.getText().trim();
            String    filtroProducto = txtFiltroProducto.getText().trim();

            // Partir siempre del historial completo
            List<Venta> resultado = ventaServicio.obtenerHistorialVentas();

            // Encadenar filtros — cada uno reduce la lista del paso anterior
            if (fechaFiltro != null) {
                resultado = resultado.stream()
                        .filter(v -> v.getFecha().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate().equals(fechaFiltro))
                        .collect(Collectors.toList());
            }

            if (!filtroCliente.isEmpty()) {
                String clienteLower = filtroCliente.toLowerCase();
                resultado = resultado.stream()
                        .filter(v -> v.getCliente().getNombre()
                                .toLowerCase().contains(clienteLower))
                        .collect(Collectors.toList());
            }

            if (!filtroProducto.isEmpty()) {
                String productoLower = filtroProducto.toLowerCase();
                resultado = resultado.stream()
                        .filter(v -> v.getDetalles().stream()
                                .anyMatch(d -> d.getProducto().getId()
                                        .toLowerCase().contains(productoLower)))
                        .collect(Collectors.toList());
            }

            tablaVentas.getItems().setAll(resultado);

        } catch (IOException e) {
            mostrarAlerta("Error al filtrar ventas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Limpia todos los campos de filtro y recarga el historial completo.
     */
    @FXML
    private void limpiarFiltros() {
        datePickerFiltro.setValue(null);
        txtFiltroCliente.clear();
        txtFiltroProducto.clear();
        cargarDatosIniciales();
    }

    // ── Tab 2: Reportes por período ───────────────────────────────────────────

    /**
     * Genera el reporte según el tipo seleccionado en el combo.
     * Valida que las fechas necesarias estén seleccionadas antes de proceder.
     * El resultado se muestra en el TextArea como texto plano.
     */
    @FXML
    private void generarReportePeriodo() {
        try {
            String    tipoReporte = comboTipoReporte.getSelectionModel().getSelectedItem();
            LocalDate inicio      = datePickerInicio.getValue();
            LocalDate fin         = datePickerFin.getValue();

            VentaServicio.ReporteVentas reporte;

            switch (tipoReporte) {
                case "Diario" -> {
                    if (inicio == null) {
                        mostrarAlerta("Seleccione una fecha para el reporte diario.", Alert.AlertType.WARNING);
                        return;
                    }
                    reporte = ventaServicio.generarReporteDiario(inicio);
                }
                case "Semanal" -> {
                    if (inicio == null) {
                        mostrarAlerta("Seleccione una fecha de inicio para el reporte semanal.", Alert.AlertType.WARNING);
                        return;
                    }
                    reporte = ventaServicio.generarReporteSemanal(inicio);
                }
                case "Mensual" -> {
                    if (inicio == null) {
                        mostrarAlerta("Seleccione una fecha para el reporte mensual.", Alert.AlertType.WARNING);
                        return;
                    }
                    reporte = ventaServicio.generarReporteMensual(inicio.getYear(), inicio.getMonthValue());
                }
                case "Anual" -> {
                    if (inicio == null) {
                        mostrarAlerta("Seleccione una fecha para el reporte anual.", Alert.AlertType.WARNING);
                        return;
                    }
                    reporte = ventaServicio.generarReporteAnual(inicio.getYear());
                }
                case "Personalizado" -> {
                    if (inicio == null || fin == null) {
                        mostrarAlerta("Seleccione fecha de inicio y fin.", Alert.AlertType.WARNING);
                        return;
                    }
                    List<Venta> ventas = ventaServicio.filtrarVentasPorRangoFechas(inicio, fin);
                    reporte = new VentaServicio.ReporteVentas(
                            "REPORTE PERSONALIZADO — " + inicio + " a " + fin, ventas);
                }
                default -> { return; }
            }

            mostrarReporte(reporte);

        } catch (IOException e) {
            mostrarAlerta("Error al generar reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Formatea un ReporteVentas y lo muestra en el TextArea.
     * Incluye encabezado con totales y detalle línea por línea.
     */
    private void mostrarReporte(VentaServicio.ReporteVentas reporte) {
        StringBuilder sb = new StringBuilder();
        sb.append(reporte.getTitulo()).append("\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("Total de ventas  : ").append(reporte.getTotalVentas()).append("\n");
        sb.append("Ingresos totales : $").append(reporte.getTotalIngresos()).append("\n");
        sb.append("=".repeat(50)).append("\n\n");
        sb.append("DETALLE:\n").append("-".repeat(50)).append("\n");

        for (Venta venta : reporte.getVentas()) {
            sb.append(String.format("ID: %-12s | Fecha: %-12s | Cliente: %-20s | Total: $%.2f%n",
                    venta.getIdVenta(),
                    venta.getFecha().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    venta.getCliente().getNombre(),
                    venta.calcularTotal()));
        }

        txtAreaReporte.setText(sb.toString());
    }

    // ── Tab 3: Resumen por producto ───────────────────────────────────────────

    /**
     * Genera el resumen de ventas agrupado por producto en el rango de fechas indicado.
     * Muestra cantidad total vendida e ingresos por producto, más el ingreso global.
     */
    @FXML
    private void generarResumenProductos() {
        try {
            LocalDate inicio = datePickerResumenInicio.getValue();
            LocalDate fin    = datePickerResumenFin.getValue();

            if (inicio == null || fin == null) {
                mostrarAlerta("Seleccione un rango de fechas para el resumen.", Alert.AlertType.WARNING);
                return;
            }

            Map<String, VentaServicio.ResumenProducto> resumenMap =
                    ventaServicio.generarResumenPorProducto(inicio, fin);

            BigDecimal ingresosTotales = ventaServicio.calcularIngresosAcumulados(inicio, fin);
            lblTotalIngresos.setText("Ingresos Totales: $" + ingresosTotales.toPlainString());

            List<ResumenProductoTabla> resumenLista = resumenMap.values().stream()
                    .map(rp -> new ResumenProductoTabla(
                            rp.getNombre(),
                            rp.getCategoria(),
                            rp.getTotalCantidad(),
                            rp.getTotalVentas()))
                    .collect(Collectors.toList());

            tablaResumenProductos.getItems().setAll(resumenLista);

        } catch (IOException e) {
            mostrarAlerta("Error al generar resumen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Muestra un diálogo de alerta con el mensaje y tipo indicados.
     * Se usa en lugar de setAlert() de MenuController para mantener
     * el control del título y encabezado desde este controlador.
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Reportes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ── Clase auxiliar para la tabla de resumen ───────────────────────────────

    /**
     * Clase interna que representa una fila en la tabla de resumen por producto.
     * Es estática porque no necesita acceder a ningún miembro de ReportesController.
     * Los getters siguen la convención de nombres para que PropertyValueFactory los encuentre.
     */
    public static class ResumenProductoTabla {
        private final String     nombre;
        private final String     categoria;
        private final int        cantidad;
        private final BigDecimal total;

        public ResumenProductoTabla(String nombre, String categoria, int cantidad, BigDecimal total) {
            this.nombre    = nombre;
            this.categoria = categoria;
            this.cantidad  = cantidad;
            this.total     = total;
        }

        public String     getNombre()    { return nombre; }
        public String     getCategoria() { return categoria; }
        public int        getCantidad()  { return cantidad; }
        public BigDecimal getTotal()     { return total; }
    }
}
