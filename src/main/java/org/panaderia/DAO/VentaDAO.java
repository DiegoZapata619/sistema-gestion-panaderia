package org.panaderia.DAO;

import org.panaderia.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * Estructura del CSV — separador ";", productos múltiples con "|":
 *   idVenta;fecha;idCliente;nombreCliente;nombreEmpleado;metodoPago;
 *   idsProductos;cantidades;precios;subtotal;descuento;total
 *
 * La ruta se resuelve una sola vez con System.getProperty("user.dir")
 * para garantizar consistencia entre sistemas operativos y máquinas.
 */
public class VentaDAO implements CRUD<Venta, String> {

    private static final String SEPARADOR = ";";
    private static final String SEP_LISTA = "\\|";    // para split
    private static final String SEP_LISTA_ESC = "|";      // para escribir
    private static final String ENCABEZADO =
            "idVenta;fecha;idCliente;nombreCliente;nombreEmpleado;metodoPago;" +
                    "idsProductos;cantidades;precios;subtotal;descuento;total";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Ruta resuelta una sola vez — válida en cualquier máquina.
     */
    private static final String VENTAS_FILE =
            System.getProperty("user.dir") + "/data/ventas.csv";
    private static final String PRODUCTOS_FILE =
            System.getProperty("user.dir") + "/data/productos.csv";
    private final ProductDAO productoDAO;

    // ── Constructores ─────────────────────────────────────────────────────────

    /**
     * Constructor completo: con acceso al catálogo de productos.
     */
    public VentaDAO(ProductDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    /**
     * Constructor sin catálogo: los productos se reconstruyen solo con id y precio del CSV.
     */
    public VentaDAO() {
        this.productoDAO = null;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Lee todas las ventas del archivo CSV indicado.
     * Si el archivo no existe devuelve una lista vacía — es un estado válido
     * al iniciar el sistema por primera vez.
     */
    @Override
    public List<Venta> leer(String ruta) throws IOException {
        List<Venta> ventas = new ArrayList<>();
        File archivo = new File(ruta);
        if (!archivo.exists()) return ventas;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            br.readLine(); // saltar encabezado
            
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                String[] datos = linea.split(SEPARADOR);
                if (datos.length >= 12) {
                    Venta venta = parsearVentaDesdeCSV(datos);
                    if (venta != null) ventas.add(venta);
                }
            }
        }
        return ventas;
    }

    /**
     * Reescribe el archivo completo con la lista de ventas proporcionada.
     */
    @Override
    public void guardar(String ruta, List<Venta> elementos) throws IOException {
        File archivo = new File(ruta);
        archivo.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write(ENCABEZADO);
            bw.newLine();
            for (Venta venta : elementos) {
                bw.write(ventaACSV(venta));
                bw.newLine();
            }
        }
    }

    /**
     * Agrega una venta al final del CSV sin reescribir el archivo completo.
     * Si el archivo no existe lo crea con encabezado.
     */
    @Override
    public void agregar(String ruta, Venta nuevo) throws IOException {
        File archivo = new File(ruta);
        archivo.getParentFile().mkdirs();
        boolean nuevoArchivo = !archivo.exists();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            if (nuevoArchivo) {
                bw.write(ENCABEZADO);
                bw.newLine();
            }
            bw.write(ventaACSV(nuevo));
            bw.newLine();
        }
    }

    /**
     * Elimina la venta con el ID indicado y reescribe el archivo.
     */
    @Override
    public boolean eliminar(String ruta, String id) throws IOException {
        if (!new File(ruta).exists()) return false;
        List<Venta> ventas = leer(ruta);
        boolean eliminado = ventas.removeIf(v -> v.getIdVenta().equals(id));
        if (eliminado) guardar(ruta, ventas);
        return eliminado;
    }

    /**
     * Reemplaza la venta con el mismo ID por la versión actualizada.
     */
    @Override
    public boolean actualizar(String ruta, Venta actualizado) throws IOException {
        if (!new File(ruta).exists()) return false;
        List<Venta> ventas = leer(ruta);
        boolean encontrado = false;
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getIdVenta().equals(actualizado.getIdVenta())) {
                ventas.set(i, actualizado);
                encontrado = true;
                break;
            }
        }
        if (encontrado) guardar(ruta, ventas);
        return encontrado;
    }

    // ── Parseo ────────────────────────────────────────────────────────────────

    /**
     * Reconstruye un objeto Venta desde un arreglo de campos del CSV.
     * <p>
     * Indices:
     * 0=idVenta, 1=fecha, 2=idCliente, 3=nombreCliente, 4=nombreEmpleado,
     * 5=metodoPago, 6=idsProductos, 7=cantidades, 8=precios,
     * 9=subtotal, 10=descuento, 11=total
     * <p>
     * Los productos se enriquecen desde ProductoDAO cuando esta disponible,
     * garantizando que la categoria y demas atributos sean los del catalogo real.
     * Si el producto no existe en el catalogo (fue eliminado), se crea un objeto
     * ligero con los datos disponibles en el CSV como fallback.
     */
    private Venta parsearVentaDesdeCSV(String[] datos) {
        try {
            // ── Fecha ────────────────────────────────────────────────────────
            LocalDateTime fechaLeida = LocalDateTime.parse(datos[1].trim(), FORMATO_FECHA);
            java.util.Date fechaDate = java.util.Date.from(
                    fechaLeida.atZone(ZoneId.systemDefault()).toInstant());

            // ── Cliente y Empleado ligeros ────────────────────────────────────
            Cliente cliente = new Cliente(datos[2].trim(), datos[3].trim());
            Empleado empleado = new Empleado(datos[4].trim(), Rol.EMPLEADO);
            MetodoPago metodo = MetodoPago.valueOf(datos[5].trim());

            // ── Venta ─────────────────────────────────────────────────────────
            Venta venta = new Venta(datos[0].trim(), cliente, empleado, metodo);
            venta.setFecha(fechaDate);

            // ── Detalles: productos, cantidades y precios separados por "|" ──
            String[] idsProductos = datos[6].split(SEP_LISTA);
            String[] cantidades = datos[7].split(SEP_LISTA);
            String[] precios = datos[8].split(SEP_LISTA);

            int minLength = Math.min(idsProductos.length, Math.min(cantidades.length, precios.length));
            
            for (int i = 0; i < minLength; i++) {
                String id = idsProductos[i].trim();
                if (id.isEmpty()) continue;
                
                int cantidad = Integer.parseInt(cantidades[i].trim());
                double precio = Double.parseDouble(precios[i].trim());

                Producto producto = resolverProducto(id, precio);
                venta.agregarDetalle(producto, cantidad);
            }

            // ── Descuento ─────────────────────────────────────────────────────
            String descuentoStr = datos[10].trim();
            if (!descuentoStr.isEmpty() && !descuentoStr.equals("0.00")) {
                venta.setDescuentoAplicado(new BigDecimal(descuentoStr));
            }

            return venta;

        } catch (Exception e) {
            System.err.println("Error parseando venta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Resuelve un Producto dado su ID.
     * Si hay ProductoDAO disponible, busca el producto real en el catálogo
     * para obtener nombre, categoría y demás atributos correctos.
     * Si no lo encuentra (producto eliminado) o no hay DAO, crea un objeto
     * ligero con el precio del CSV como fallback.
     */
    private Producto resolverProducto(String id, double precioCSV) {
        if (productoDAO != null) {
            try {
                Optional<Producto> encontrado = productoDAO.buscarPorId(id);
                if (encontrado.isPresent()) return encontrado.get();
            } catch (IOException e) {
                System.err.println("No se pudo cargar producto " + id + " del catálogo: " + e.getMessage());
            }
        }
        // Fallback: producto ligero con solo los datos del CSV
        return new Producto(id, precioCSV);
    }

    /**
     * Convierte una Venta a una línea CSV.
     * Múltiples productos se serializan con "|" como separador interno.
     */
    private String ventaACSV(Venta venta) {
        StringBuilder ids = new StringBuilder();
        StringBuilder cantidades = new StringBuilder();
        StringBuilder precios = new StringBuilder();

        for (DetalleVenta detalle : venta.getDetalles()) {
            ids.append(detalle.getProducto().getId()).append(SEP_LISTA_ESC);
            cantidades.append(detalle.getCantidad()).append(SEP_LISTA_ESC);
            precios.append(detalle.getPrecioUnitario()).append(SEP_LISTA_ESC);
        }

        // Quitar el "|" final de cada sub-campo
        if (ids.length() > 0) {
            ids.setLength(ids.length() - 1);
            cantidades.setLength(cantidades.length() - 1);
            precios.setLength(precios.length() - 1);
        }

        // Formatear la fecha sin segundos
        String fechaStr = venta.getFecha().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMATO_FECHA);

        return venta.getIdVenta() + ";" +
                fechaStr + ";" +
                venta.getCliente().getId() + ";" +
                venta.getCliente().getNombre() + ";" +
                venta.getEmpleado().getNombre() + ";" +
                venta.getMetodoPago() + ";" +
                ids + ";" +
                cantidades + ";" +
                precios + ";" +
                venta.calcularSubtotal() + ";" +
                venta.getDescuentoAplicado() + ";" +
                venta.calcularTotal();
    }

    // ── Consultas para reportes ───────────────────────────────────────────────

    /**
     * Devuelve todas las ventas registradas.
     */
    public List<Venta> obtenerTodas() throws IOException {
        return leer(VENTAS_FILE);
    }

    /**
     * Filtra ventas cuya fecha coincide exactamente con el día indicado.
     */
    public List<Venta> obtenerVentasPorFecha(LocalDate fecha) throws IOException {
        return leer(VENTAS_FILE).stream()
                .filter(v -> toLocalDate(v).equals(fecha))
                .collect(Collectors.toList());
    }

    /**
     * Filtra ventas dentro de un rango de fechas inclusivo (inicio ≤ fecha ≤ fin).
     */
    public List<Venta> obtenerVentasPorRangoFechas(LocalDate inicio, LocalDate fin) throws IOException {
        return leer(VENTAS_FILE).stream()
                .filter(v -> {
                    LocalDate f = toLocalDate(v);
                    return !f.isBefore(inicio) && !f.isAfter(fin);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filtra ventas asociadas a un cliente por su ID.
     */
    public List<Venta> obtenerVentasPorCliente(String idCliente) throws IOException {
        return leer(VENTAS_FILE).stream()
                .filter(v -> v.getCliente().getId().equals(idCliente))
                .collect(Collectors.toList());
    }

    /**
     * Filtra ventas que contengan al menos un detalle con el producto indicado.
     */
    public List<Venta> obtenerVentasPorProducto(String idProducto) throws IOException {
        return leer(VENTAS_FILE).stream()
                .filter(v -> v.getDetalles().stream()
                        .anyMatch(d -> d.getProducto().getId().equals(idProducto)))
                .collect(Collectors.toList());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Convierte la fecha de una Venta a LocalDate usando la zona horaria del sistema.
     */
    private LocalDate toLocalDate(Venta venta) {
        return venta.getFecha().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}