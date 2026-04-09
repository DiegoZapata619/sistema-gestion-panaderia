package org.panaderia.model;

import org.panaderia.Servicios.LectorArchivos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements LectorArchivos {

    @Override
    public List<Cliente> leer(String ruta) throws IOException {
        ArrayList<Cliente> clientes = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {

            lector.readLine(); // encabezado

            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] p = linea.split(";");

                if (p.length < 7) continue;

                clientes.add(new Cliente(
                        p[0], p[1], p[2], p[3], p[4],
                        Integer.parseInt(p[5]),
                        Integer.parseInt(p[6])
                ));
            }
        }
        return clientes;
    }

    public void guardarClientes(String ruta, List<Cliente> clientes) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

            bw.write("id;nombre;telefono;correo;preferencias;puntos;visitas");
            bw.newLine();

            for (Cliente c : clientes) {
                bw.write(
                        c.getId() + ";" +
                                c.getNombre() + ";" +
                                c.getTelefono() + ";" +
                                c.getCorreo() + ";" +
                                c.getPreferencias() + ";" +
                                c.getPuntos() + ";" +
                                c.getVisitas()
                );
                bw.newLine();
            }
        }
    }

    public void agregarCliente(String ruta, Cliente nuevo) throws IOException {

        List<Cliente> clientes = leer(ruta);

        for (Cliente c : clientes) {
            if (c.getId().equals(nuevo.getId())) {
                throw new IllegalArgumentException("Cliente duplicado");
            }
        }

        clientes.add(nuevo);
        guardarClientes(ruta, clientes);
    }

    public boolean eliminarCliente(String ruta, String id) throws IOException {

        List<Cliente> clientes = leer(ruta);

        boolean eliminado = clientes.removeIf(c -> c.getId().equals(id));

        if (eliminado) {
            guardarClientes(ruta, clientes);
        }

        return eliminado;
    }

    public boolean actualizarCliente(String ruta, Cliente actualizado) throws IOException {

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
            guardarClientes(ruta, clientes);
        }

        return encontrado;
    }
}