package org.panaderia.Servicios;

import org.panaderia.model.Cliente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorCSVCliente implements LectorArchivos<Cliente> {

    @Override
    public List<Cliente> leer(String ruta) throws IOException {

        List<Cliente> clientes = new ArrayList<>();

        try (BufferedReader lectorCSV = new BufferedReader(new FileReader(ruta))) {

            // Saltar encabezado
            lectorCSV.readLine();

            String linea;

            while ((linea = lectorCSV.readLine()) != null) {

                linea = linea.trim();

                // Saltar líneas vacías
                if (linea.isEmpty()) {
                    continue;
                }

                // Separador con ;
                String[] partes = linea.split(";");

                // Validar que tenga todos los campos
                if (partes.length < 7) {
                    continue;
                }

                // Campos del cliente
                String id = partes[0].trim();
                String nombre = partes[1].trim();
                String telefono = partes[2].trim();
                String correo = partes[3].trim();
                String preferencias = partes[4].trim();

                int puntos;
                int visitas;

                try {
                    puntos = Integer.parseInt(partes[5].trim());
                    visitas = Integer.parseInt(partes[6].trim());
                } catch (NumberFormatException e) {
                    // Si hay error en números, se salta el registro
                    continue;
                }

                Cliente cliente = new Cliente(
                        id, nombre, telefono, correo,
                        preferencias, puntos, visitas
                );

                clientes.add(cliente);
            }
        }

        return clientes;
    }
}