package org.panaderia.DAO;


import org.panaderia.model.Administrador;
import org.panaderia.model.Empleado;
import org.panaderia.model.Rol;
import org.panaderia.model.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements CRUD<Usuario, String> {

    private static final String ENCABEZADO = "nombre,hashPassword,rol";

    @Override
    public List<Usuario> leer(String ruta) throws IOException {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            lector.readLine();
            String linea;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                String nombre = partes[0];
                String hashPassword = partes[1];
                Rol rol = Rol.valueOf(partes[2]);

                if (rol == Rol.ADMINISTRADOR) {
                    usuarios.add(new Administrador(nombre, hashPassword, rol));
                } else if (rol == Rol.EMPLEADO) {
                    usuarios.add(new Empleado(nombre, hashPassword, rol));
                }
            }
        }
        return usuarios;
    }

    @Override
    public void guardar(String ruta, List<Usuario> usuarios) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write(ENCABEZADO);
            bw.newLine();

            for (Usuario u : usuarios) {
                bw.write(
                        u.getNombre() + "," +
                                u.getHashPassword() + "," +
                                u.getRol()
                );
                bw.newLine();
            }
        }
    }

    @Override
    public void agregar(String ruta, Usuario nuevo) throws IOException {
        List<Usuario> usuarios = leer(ruta);

        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nuevo.getNombre())) {
                throw new IllegalArgumentException("Producto duplicado");
            }
        }

        usuarios.add(nuevo);
        guardar(ruta, usuarios);

    }

    @Override
    public boolean eliminar(String ruta, String nombre) throws IOException {
        List<Usuario> usuarios = leer(ruta);

        boolean eliminado = usuarios.removeIf(u -> u.getNombre().equals(nombre));

        if (eliminado) {
            guardar(ruta, usuarios);
        }

        return eliminado;
    }

    @Override
    public boolean actualizar(String ruta, Usuario actualizado) throws IOException {
        List<Usuario> usuarios = leer(ruta);
        boolean encontrado = false;

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getNombre().equals(actualizado.getNombre())) {
                usuarios.set(i, actualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            guardar(ruta, usuarios);
        }

        return encontrado;
    }
}

