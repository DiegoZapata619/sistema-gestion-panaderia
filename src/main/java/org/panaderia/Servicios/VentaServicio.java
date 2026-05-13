package org.panaderia.Servicios;

import org.panaderia.DAO.VentaDAO;
import org.panaderia.DAO.ProductDAO;
import org.panaderia.model.Venta;
import org.panaderia.model.DetalleVenta;
import org.panaderia.model.Producto;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class VentaServicio {
    private final VentaDAO ventaDAO;
    private final ProductDAO productDAO;
    private final String RUTA_VENTA;

    public VentaServicio() {
        this.productDAO = new ProductDAO();
        this.ventaDAO = new VentaDAO(productDAO);
        this.RUTA_VENTA = System.getProperty("user.dir") + "/data/ventas.csv";
    }

    /**
     * RF-3.1.4.1: Registrar una venta completa
     */
    public void registrarVenta(Venta venta) throws IOException {
        // Actualizar inventario
        actualizarInventario(venta);
        
        // Guardar la venta
        ventaDAO.agregar(RUTA_VENTA, venta);
        
        // Generar comprobante
        generarComprobante(venta);
    }

    /**
     * RF-3.1.4.2: Obtener historial completo de ventas
     */
    public List<Venta> obtenerHistorialVentas() throws IOException {
        return ventaDAO.leer(RUTA_VENTA);
    }

    /**
     * RF-3.1.4.3: Consultar ventas anteriores (con permisos)
     */
    public List<Venta> consultarVentasAnteriores(String rolUsuario) throws IOException {
        List<Venta> todasLasVentas = obtenerHistorialVentas();
        
        // Si es empleado, solo puede ver ventas del día actual
        if ("EMPLEADO".equals(rolUsuario)) {
            LocalDate hoy = LocalDate.now();
            return todasLasVentas.stream()
                    .filter(venta -> venta.getFecha().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate().equals(hoy))
                    .collect(Collectors.toList());
        }
        
        // Si es administrador, puede ver todas las ventas
        return todasLasVentas;
    }

    /**
     * RF-3.1.4.4: Filtrar ventas por diferentes criterios
     */
    public List<Venta> filtrarVentasPorFecha(LocalDate fecha) throws IOException {
        return ventaDAO.obtenerVentasPorFecha(fecha);
    }

    public List<Venta> filtrarVentasPorCliente(String idCliente) throws IOException {
        return ventaDAO.obtenerVentasPorCliente(idCliente);
    }

    public List<Venta> filtrarVentasPorProducto(String idProducto) throws IOException {
        return ventaDAO.obtenerVentasPorProducto(idProducto);
    }

    public List<Venta> filtrarVentasPorRangoFechas(LocalDate inicio, LocalDate fin) throws IOException {
        return ventaDAO.obtenerVentasPorRangoFechas(inicio, fin);
    }

    /**
     * RF-3.1.4.5: Actualizar inventario al cerrar venta
     */
    private void actualizarInventario(Venta venta) throws IOException {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            int cantidadVendida = detalle.getCantidad();
            
            // Reducir stock del producto
            int nuevoStock = producto.getStock() - cantidadVendida;
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            
            producto.setStock(nuevoStock);
            // Actualizar producto en el DAO
            // productDAO.actualizar("data/productos.csv", producto);
        }
    }

    /**
     * Generar comprobante de venta
     */
    private void generarComprobante(Venta venta) {
        String comprobante = String.format(
            "COMPROBANTE DE VENTA\n" +
            "========================\n" +
            "ID Venta: %s\n" +
            "Fecha: %s\n" +
            "Cliente: %s\n" +
            "Empleado: %s\n" +
            "Método Pago: %s\n" +
            "------------------------\n" +
            "DETALLE:\n" +
            "%s\n" +
            "------------------------\n" +
            "Subtotal: $%.2f\n" +
            "Descuento: $%.2f\n" +
            "Total: $%.2f\n" +
            "========================",
            venta.getIdVenta(),
            venta.getFecha().toString(),
            venta.getCliente().getNombre(),
            venta.getEmpleado().getNombre(),
            venta.getMetodoPago().toString(),
            generarDetalleComprobante(venta),
            venta.calcularSubtotal(),
            venta.getDescuentoAplicado(),
            venta.calcularTotal()
        );
        
        // Guardar comprobante en archivo
        // guardarComprobanteArchivo(venta.getIdVenta(), comprobante);
    }

    private String generarDetalleComprobante(Venta venta) {
        StringBuilder detalle = new StringBuilder();
        for (DetalleVenta det : venta.getDetalles()) {
            detalle.append(String.format("%s x%d = $%.2f\n",
                det.getProducto().getNombre(),
                det.getCantidad(),
                det.getSubtotal()));
        }
        return detalle.toString();
    }

    /**
     * RF-3.1.8.1: Generar reportes por período
     */
    public ReporteVentas generarReporteDiario(LocalDate fecha) throws IOException {
        List<Venta> ventasDia = filtrarVentasPorFecha(fecha);
        return new ReporteVentas("REPORTE DIARIO - " + fecha, ventasDia);
    }

    public ReporteVentas generarReporteSemanal(LocalDate inicioSemana) throws IOException {
        LocalDate finSemana = inicioSemana.plusDays(6);
        List<Venta> ventasSemana = filtrarVentasPorRangoFechas(inicioSemana, finSemana);
        return new ReporteVentas("REPORTE SEMANAL - " + inicioSemana + " a " + finSemana, ventasSemana);
    }

    public ReporteVentas generarReporteMensual(int ano, int mes) throws IOException {
        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fin = yearMonth.atEndOfMonth();
        List<Venta> ventasMes = filtrarVentasPorRangoFechas(inicio, fin);
        return new ReporteVentas("REPORTE MENSUAL - " + yearMonth, ventasMes);
    }

    public ReporteVentas generarReporteAnual(int ano) throws IOException {
        LocalDate inicio = LocalDate.of(ano, 1, 1);
        LocalDate fin = LocalDate.of(ano, 12, 31);
        List<Venta> ventasAno = filtrarVentasPorRangoFechas(inicio, fin);
        return new ReporteVentas("REPORTE ANUAL - " + ano, ventasAno);
    }

    /**
     * RF-3.1.8.2: Resúmenes por producto y categoría
     */
    public Map<String, ResumenProducto> generarResumenPorProducto(LocalDate inicio, LocalDate fin) throws IOException {
        List<Venta> ventas = filtrarVentasPorRangoFechas(inicio, fin);
        Map<String, ResumenProducto> resumen = new HashMap<>();

        for (Venta venta : ventas) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                String idProducto = detalle.getProducto().getId();
                String nombreProducto = detalle.getProducto().getNombre();
                String categoria = detalle.getProducto().getCategoria();
                
                resumen.computeIfAbsent(idProducto, k -> new ResumenProducto(nombreProducto, categoria))
                        .agregarVenta(detalle.getCantidad(), detalle.getSubtotal());
            }
        }
        
        return resumen;
    }

    /**
     * RF-3.1.8.3: Consultar ingresos acumulados por rango de fechas
     */
    public BigDecimal calcularIngresosAcumulados(LocalDate inicio, LocalDate fin) throws IOException {
        List<Venta> ventas = filtrarVentasPorRangoFechas(inicio, fin);
        return ventas.stream()
                .map(Venta::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Clases internas para reportes
    public static class ReporteVentas {
        private final String titulo;
        private final List<Venta> ventas;
        private final BigDecimal totalIngresos;
        private final int totalVentas;

        public ReporteVentas(String titulo, List<Venta> ventas) {
            this.titulo = titulo;
            this.ventas = ventas;
            this.totalIngresos = ventas.stream()
                    .map(Venta::calcularTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalVentas = ventas.size();
        }

        // Getters
        public String getTitulo() { return titulo; }
        public List<Venta> getVentas() { return ventas; }
        public BigDecimal getTotalIngresos() { return totalIngresos; }
        public int getTotalVentas() { return totalVentas; }
    }

    public static class ResumenProducto {
        private final String nombre;
        private final String categoria;
        private int totalCantidad;
        private BigDecimal totalVentas;

        public ResumenProducto(String nombre, String categoria) {
            this.nombre = nombre;
            this.categoria = categoria;
            this.totalCantidad = 0;
            this.totalVentas = BigDecimal.ZERO;
        }

        public void agregarVenta(int cantidad, BigDecimal monto) {
            this.totalCantidad += cantidad;
            this.totalVentas = this.totalVentas.add(monto);
        }

        // Getters
        public String getNombre() { return nombre; }
        public String getCategoria() { return categoria; }
        public int getTotalCantidad() { return totalCantidad; }
        public BigDecimal getTotalVentas() { return totalVentas; }
    }
}
