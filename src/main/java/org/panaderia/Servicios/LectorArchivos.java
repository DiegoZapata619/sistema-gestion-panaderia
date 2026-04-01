package org.panaderia.Servicios;

import java.io.IOException;
import java.util.List;

//Para no depender de una implementación concreta, se define una interfaz para lectura.
//En un proyecto mayor, se utilizaria conexion a base de datos
public interface LectorArchivos <T>{
    List<T> leer(String ruta) throws IOException;
}
