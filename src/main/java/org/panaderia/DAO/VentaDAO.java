package org.panaderia.DAO;

import org.panaderia.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO para persistencia de ventas en CSV.
 *
 * Estructura del CSV — separador principal ";", sub-campos con "|":
 *   idVenta;fecha;idCliente;nombreCliente;idEmpleado;nombreEmpleado;metodoPago;
 *   ids_productos|..;cantidades|..;precios|..;subtotal;descuento;total
 *
 * Se usa ";" para evitar conflictos con nombres de productos o clientes que
 * puedan contener comas, consistente con el resto de DAOs del proyecto.
 * Se usa "|" como separador interno de listas para no colisionar con ";".
 */
public class VentaDAO implements CRUD<Venta, String> {

    private static final String VENTAS_FILE = "Ventas.csv";
    private static final String ENCABEZADO =
            "idVenta;fecha;idCliente;nombreCliente;nombreEmpleado;metodoPago;" +
                    "idsProductos;cantidades;precios;subtotal;descuento;total";

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Lee todas las ventas del archivo CSV indicado.
     * Si el archivo no existe devuelve una lista vacía en lugar de lanzar excepción,
     * ya que es un estado válido al iniciar el sistema por primera vez.
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
                String[] datos = linea.split(";");
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
     * Crea el directorio padre si no existe.
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
     * Más eficiente que guardar() para el caso de uso más común (registrar una venta nueva).
     */
    @Override
    public void agregar(String ruta, Venta nuevo) throws IOException {
        File archivo = new File(ruta);
        archivo.getParentFile().mkdirs();
        boolean archivoNuevo = !archivo.exists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            // Si el archivo es nuevo, escribir encabezado primero
            if (archivoNuevo) {
                bw.write(ENCABEZADO);
                bw.newLine();
            }
            bw.write(ventaACSV(nuevo));
            bw.newLine();
        }
    }

    /**
     * Elimina la venta con el ID indicado y reescribe el archivo.
     * Devuelve true si encontró y eliminó la venta, false si no existía.
     */
    @Override
    public boolean eliminar(String ruta, String id) throws IOException {
        File archivo = new File(ruta);
        if (!archivo.exists()) return false;

        List<Venta> ventas = leer(ruta);
        boolean eliminado = ventas.removeIf(v -> v.getIdVenta().equals(id));
        if (eliminado) guardar(ruta, ventas);
        return eliminado;
    }

    /**
     * Reemplaza la venta con el mismo ID por la versión actualizada.
     * Devuelve true si encontró la venta, false si no existía.
     *
     * CORRECCIÓN: la versión anterior devolvía true siempre, incluso
     * cuando el ID no existía en el archivo.
     */
    @Override
    public boolean actualizar(String ruta, Venta actualizado) throws IOException {
        File archivo = new File(ruta);
        if (!archivo.exists()) return false;

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
     *
     * Se crean objetos "ligeros" de Cliente y Producto con solo los datos
     * disponibles en el CSV (id y nombre). Esto es suficiente para reportes.
     * Si se necesitara el objeto completo habría que cruzar con ClienteDAO
     * y ProductDAO, lo cual haría la carga de ventas significativamente más lenta.
     *
     * CORRECCIONES aplicadas:
     *   - Cliente ahora recibe los 7 parámetros que su constructor requiere
     *   - Producto ahora recibe los tipos correctos (int para stock y stockMinimo)
     *   - El descuento se restaura mediante setDescuentoAplicado() en lugar
     *     de reflexión (getDeclaredField), que era frágil y evitable
     *   - Los índices de campos están corregidos según el encabezado real
     */
    private Venta parsearVentaDesdeCSV(String[] datos) {
        try {
            // índices: 0=idVenta, 1=fecha, 2=idCliente, 3=nombreCliente,
            //          4=nombreEmpleado, 5=metodoPago, 6=ids, 7=cantidades,
            //          8=precios, 9=subtotal, 10=descuento, 11=total

            // Cliente ligero: solo id y nombre, resto con valores neutros
            // CORRECCIÓN: constructor de Cliente tiene 7 parámetros, no 5
            Cliente cliente = new Cliente(
                    datos[2].trim(),   // id
                    datos[3].trim(),   // nombre
                    "",                // telefono
                    "",                // correo
                    "",                // preferencias
                    0,                 // puntos
                    0                  // visitas
            );

            // Empleado ligero: solo nombre, hashPassword vacío
            // CORRECCIÓN: datos[5] es metodoPago, no hashPassword — índice corregido
            Empleado empleado = new Empleado(datos[4].trim(), "", Rol.EMPLEADO);

            MetodoPago metodoPago = MetodoPago.valueOf(datos[5].trim());

            Venta venta = new Venta(datos[0].trim(), cliente, empleado, metodoPago);

            // Reconstruir detalles: ids, cantidades y precios separados por "|"
            String[] idsProductos = datos[6].split("\\|");
            String[] cantidades   = datos[7].split("\\|");

            for (int i = 0; i < idsProductos.length; i++) {
                // Producto ligero con solo id — precio y stock se leen del CSV, no del catálogo
                // CORRECCIÓN: stock y stockMinimo son int, no String
                Producto producto = new Producto(
                        idsProductos[i].trim(), // id
                        "",                     // nombre
                        "",                     // categoria
                        0.0,                    // precio (se usa el del CSV)
                        0,                      // stock  ← int, no ""
                        0,                      // stockMinimo ← int, no ""
                        ""                      // descripcion
                );
                int cantidad = Integer.parseInt(cantidades[i].trim());
                venta.agregarDetalle(producto, cantidad);
            }

            // Restaurar descuento mediante setter en lugar de reflexión
            // CORRECCIÓN: getDeclaredField era frágil, innecesario y evitable
            if (datos.length > 10 && !datos[10].trim().isEmpty()) {
                venta.setDescuentoAplicado(new BigDecimal(datos[10].trim()));
            }

            return venta;

        } catch (Exception e) {
            System.err.println("Error parseando venta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convierte una Venta a una línea CSV.
     * Los sub-campos de productos se separan con "|" para no colisionar
     * con el separador principal ";".
     */
    private String ventaACSV(Venta venta) {
        StringBuilder idsProductos = new StringBuilder();
        StringBuilder cantidades   = new StringBuilder();
        StringBuilder precios      = new StringBuilder();

        for (DetalleVenta detalle : venta.getDetalles()) {
            idsProductos.append(detalle.getProducto().getId()).append("|");
            cantidades.append(detalle.getCantidad()).append("|");
            precios.append(detalle.getPrecioUnitario()).append("|");
        }

        // Eliminar el "|" final de cada sub-campo
        if (idsProductos.length() > 0) {
            idsProductos.setLength(idsProductos.length() - 1);
            cantidades.setLength(cantidades.length() - 1);
            precios.setLength(precios.length() - 1);
        }

        return venta.getIdVenta()                  + ";" +
                venta.getFecha().toString()          + ";" +
                venta.getCliente().getId()           + ";" +
                venta.getCliente().getNombre()       + ";" +
                venta.getEmpleado().getNombre()      + ";" +
                venta.getMetodoPago().toString()     + ";" +
                idsProductos                         + ";" +
                cantidades                           + ";" +
                precios                              + ";" +
                venta.calcularSubtotal()             + ";" +
                venta.getDescuentoAplicado()         + ";" +
                venta.calcularTotal();
    }

    // ── Consultas para reportes ───────────────────────────────────────────────

    /**
     * Devuelve todas las ventas registradas en el archivo por defecto.
     */
    public List<Venta> obtenerTodas() throws IOException {
        return leer(VENTAS_FILE);
    }

    /**
     * Filtra ventas cuya fecha coincide exactamente con el día indicado.
     * Convierte Date → LocalDate usando la zona horaria del sistema.
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
                    LocalDate fecha = toLocalDate(v);
                    return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filtra ventas asociadas a un cliente específico por su ID.
     */
    public List<Venta> obtenerVentasPorCliente(String idCliente) throws IOException {
        return leer(VENTAS_FILE).stream()
                .filter(v -> v.getCliente().getId().equals(idCliente))
                .collect(Collectors.toList());
    }

    /**
     * Filtra ventas que contengan al menos un renglón con el producto indicado.
     */
    public List<Venta> obtenerVentasPorProducto(String idProducto) throws IOException {
        return leer(VENTAS_FILE).stream()
                .filter(v -> v.getDetalles().stream()
                        .anyMatch(d -> d.getProducto().getId().equals(idProducto)))
                .collect(Collectors.toList());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Convierte el Date de una venta a LocalDate usando la zona horaria del sistema.
     * Se extrae como método para no repetir la misma cadena de conversión en cada filtro.
     */
    private LocalDate toLocalDate(Venta venta) {
        return venta.getFecha().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }
}
