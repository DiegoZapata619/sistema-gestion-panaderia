package org.panaderia.DAO;

import org.panaderia.model.Descuentos.DescuentoFactory;
import org.panaderia.model.Descuentos.IEstrategiaDescuento;
import org.panaderia.model.Descuentos.TipoDescuento;
import org.panaderia.model.Promocion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PromocionDAO implements CRUD<Promocion, String> {

    private static final String ENCABEZADO = "id;nombre;activa;tipo;parametro1;parametro2";

    @Override
    public List<Promocion> leer(String ruta) throws IOException {
        ArrayList<Promocion> promociones = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            lector.readLine(); // saltar encabezado
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] partes = linea.split(";");
                String id         = partes[0];
                String nombre     = partes[1];
                boolean activa    = Boolean.parseBoolean(partes[2]);
                /// TipoDescuento.convertirAValorEnum() es un metodo definido en la propia clase enum
                /// valida el valor y lanza una excepción si no es un tipo de promocion valido
                TipoDescuento tipo = TipoDescuento.convertirAValorEnum(partes[3]);
                String parametro1  = partes[4];
                String parametro2  = partes[5];

                /// El metodo genérico de clase factory usa un switch-case de enum para crear una estrategia
                IEstrategiaDescuento estrategia = DescuentoFactory.crear(tipo, parametro1, parametro2);

                Promocion p = new Promocion(id, nombre, estrategia);
                p.setActivo(activa);
                promociones.add(p);
            }
        }
        return promociones;
    }


    @Override
    public void guardar(String ruta, List<Promocion> promociones) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write(ENCABEZADO);
            bw.newLine();
            for (Promocion p : promociones) {
                bw.write(serializar(p));
                bw.newLine();
            }
        }
    }

    @Override
    public void agregar(String ruta, Promocion nueva) throws IOException {
        List<Promocion> promociones = leer(ruta);
        for (Promocion p : promociones) {
            if (p.getId().equals(nueva.getId()))
                throw new IllegalArgumentException("Promoción duplicada: " + nueva.getId());
        }
        promociones.add(nueva);
        guardar(ruta, promociones);
    }

    @Override
    public boolean eliminar(String ruta, String idEliminar) throws IOException {
        List<Promocion> promociones = leer(ruta);
        boolean eliminada = promociones.removeIf(p -> p.getId().equals(idEliminar));
        if (eliminada) guardar(ruta, promociones);
        return eliminada;
    }


    @Override
    public boolean actualizar(String ruta, Promocion actualizada) throws IOException {
        List<Promocion> promociones = leer(ruta);
        boolean encontrada = false;
        for (int i = 0; i < promociones.size(); i++) {
            if (promociones.get(i).getId().equals(actualizada.getId())) {
                promociones.set(i, actualizada);
                encontrada = true;
                break;
            }
        }
        if (encontrada) guardar(ruta, promociones);
        return encontrada;
    }


    /**
     * getTipo().name() garantiza que el String escrito al CSV
     * siempre coincida con los nombres del enum, sin riesgo de typos.
     */
    private String serializar(Promocion p) {
        return p.getId()       + ";" +
                p.getNombre()   + ";" +
                p.isActivo()    + ";" +
                p.getParametros();
    }
}