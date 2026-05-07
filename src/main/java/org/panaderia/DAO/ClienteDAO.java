package org.panaderia.DAO;

import org.panaderia.model.Cliente;
import org.panaderia.model.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements CRUD<Cliente, String> {

    @Override
    public List<Cliente> leer(String ruta) throws IOException {
        ArrayList<Cliente> clientes = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            lector.readLine();
            String linea;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(";");
                String id = partes[0];
                String nombre = partes[1];
                String telefono = partes[2];
                String correo = partes[3];
                String preferencias = partes[4];
                int puntos = Integer.parseInt(partes[5]);
                int visitas = Integer.parseInt(partes[6]);

                clientes.add(new Cliente(id,nombre,telefono,correo,preferencias,puntos,visitas));
            }
        }
        return clientes;
    }

    @Override
    public void agregar(String ruta, Cliente nuevo) throws IOException {
        List<Cliente> clientes = leer(ruta);

        for (Cliente c : clientes) {
            if (c.getId().equals(nuevo.getId())) {
                throw new IllegalArgumentException("Producto duplicado");
            }
        }

        clientes.add(nuevo);
        guardar(ruta, clientes);
    }

    @Override
    public void guardar(String ruta, List<Cliente> clientes) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write("id;nombre;telefono;correo;preferencias;puntos;visitas");
            bw.newLine();

            for (Cliente c: clientes) {
                bw.write(c.getId() + ";" +
                        c.getNombre() + ";" +
                        c.getTelefono() + ";" +
                        c.getCorreo() + ";" +
                        c.getPreferencias() + ";" +
                        c.getPuntos()+ ";" +
                        c.getVisitas()
                );
                bw.newLine();
            }
        }
    }

    @Override
    public boolean eliminar(String ruta, String idEliminado) throws IOException {
        List<Cliente> clientes = leer(ruta);
        boolean eliminado = clientes.removeIf(p -> p.getId().equals(idEliminado));

        if (eliminado) {
            guardar(ruta, clientes);
        }

        return eliminado;
    }

    @Override
    public boolean actualizar(String ruta, Cliente actualizado) throws IOException {
        List<Cliente> clientes = leer(ruta);
        boolean encontrado = false;

        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId().equals(actualizado.getId())) {
                clientes.set(i, actualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            guardar(ruta, clientes);
        }
        return encontrado;
    }
}