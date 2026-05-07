package org.panaderia.DAO;


import org.panaderia.model.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements CRUD<Producto, String> {
    @Override
    public List<Producto> leer(String ruta) throws IOException {
        ArrayList<Producto> productos = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            //linea para saltar encabezado
            lector.readLine();
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                //salto lineas vacias
                if (linea.isEmpty()) {
                    continue;
                }
                //Cambio complementario
                //Para permitir descripciones de productos que contengan comas
                //Se cambia el separador por un ";" en lugar de la convención de comas como separador
                String partes[] = linea.split(";");
                String id = partes[0];
                String nombre = partes[1];
                String categoria = partes[2];
                double precio = Double.parseDouble(partes[3]);
                int stock = Integer.parseInt(partes[4]);
                int stockMinimo = Integer.parseInt(partes[5]);
                String descripcion = partes[6];
                Producto p = new Producto(id, nombre, categoria, precio, stock, stockMinimo, descripcion);
                productos.add(p);
            }

        }
        return productos;
    }
    public void guardar(String ruta, List<Producto> productos) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(ruta));
        bw.write("id,nombre,categoria,precio,stock,stockMinimo,descripcion");
        bw.newLine();
        for (Producto p : productos) {
            bw.write(
                    p.getId() + ";" +
                            p.getNombre() + ";" +
                            p.getCategoria() + ";" +
                            p.getPrecio() + ";" +
                            p.getStock() + ";" +
                            p.getStockMinimo() + ";" +
                            p.getDescripcion()
            );
            bw.newLine();
        }
        bw.close();
    }

    public void agregar(String ruta, Producto nuevo) throws IOException {
        List<Producto> productos = leer(ruta);
        for (Producto p:productos){
            if(p.getId().equals(nuevo.getId())){
                throw new IllegalArgumentException ("Producto Duplicado");
            }

        }
        productos.add(nuevo);
        guardar(ruta,productos);
    }

    public boolean eliminar(String ruta, String eliminadoId) throws IOException{
        List<Producto> productos= leer(ruta);
        //Expresion lambda. Más sencillo que desarrollar el código completo
        //removeIf busca en una coleccion y cuando encuentra un producto con la condicion cuyo
        //id coincide con el id que se busca eliminar, lo remueve de la coleccion y devuelve true
        //si no se encuentra el producto, retorna false
        boolean eliminado= productos.removeIf(p -> p.getId().equals(eliminadoId));
        if (eliminado){
            guardar(ruta,productos);
        }
        return eliminado;

    }
    public boolean actualizar(String ruta, Producto actualizado) throws IOException {
        List<Producto> productos= leer(ruta);
        boolean encontrado=false;
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equals(actualizado.getId())) {
                productos.set(i, actualizado);
                encontrado = true;
                break;
            }
        }
        if (encontrado){
            guardar(ruta,productos);
        }
        return encontrado;
    }

}
