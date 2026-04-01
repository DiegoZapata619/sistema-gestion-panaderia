package org.panaderia.Servicios;


import org.panaderia.model.Administrador;
import org.panaderia.model.Empleado;
import org.panaderia.model.Rol;
import org.panaderia.model.Usuario;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class LectorCSVUsuario implements LectorArchivos <Usuario>{
    //Metodo para lectura de .CSV
    //Se devuelve una lista de usuarios del archivo .csv
    @Override
    public List<Usuario> leer(String ruta) throws IOException {
        List<Usuario> usuarios= new ArrayList<>();
        try (BufferedReader lectorCSV = new BufferedReader(new FileReader(ruta))){
            //linea para saltar encabezado
            lectorCSV.readLine();
            String linea;
            while ((linea = lectorCSV.readLine()) != null){
                linea= linea.trim();
                //salto lineas vacias
                if (linea.isEmpty()){
                    continue;
                }
                //Si una linea no tiene los 3 campos necesarios de usuario, se salta
                String partes[] = linea.split(",");
                if (partes.length<3){
                    continue;
                }
                //Campos para usuario
                String nombre = partes[0].trim();
                String hash = partes[1].trim();
                String rol = partes[2].trim().toUpperCase();
                //El .csv está compuesto por strings, lo que era un problema
                //para el constructor de administrador o empleado
                //Se solucionó agregando Rol.valueOf()
                if (rol.equals("ADMINISTRADOR")){
                    usuarios.add(new Administrador(nombre,hash, Rol.valueOf(rol)));
                    //Abierto a extensión. Se podría modificar esta sentencia de if
                    //en caso de agregar otro tipo de usuario en un futuro para no romper programa
                } else if (rol.equals("EMPLEADO")){
                    usuarios.add(new Empleado(nombre,hash,Rol.valueOf(rol)));
                }
            }
        }
        return usuarios;
    }
}
