
package org.panaderia.DAO;

import java.io.IOException;
import java.util.List;

public interface CRUD<T, K> {
    List<T> leer(String ruta) throws IOException;

    void guardar(String ruta, List<T> elementos) throws IOException;

    void agregar(String ruta, T nuevo) throws IOException;

    boolean eliminar(String ruta, K id) throws IOException;

    boolean actualizar(String ruta, T actualizado) throws IOException;
}