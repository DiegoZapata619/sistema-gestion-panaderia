package org.panaderia.Servicios;

import org.panaderia.model.Usuario;

import java.io.IOException;
import java.util.List;

public class Autenticador {
    private final LectorCSVUsuario lector;
    private final Encriptador encriptador;
    public Autenticador (){
        this.lector= new LectorCSVUsuario();
        this.encriptador=new Encriptador();
    }
    //De la lectura del archivo .csv se devuelve un array list.
    //Este array list es usado para hacer comparaciones, usuario por usuario
    //Si coincide el nombre ingresado con el hash de la contraseña se retorna user
    //Si no coincide se retorna null
    public Usuario Autenticar(String nombre, String password) throws IOException {
        List<Usuario> usuarios= lector.leer("Users.csv");
        String hashIngresado= encriptador.sha256(password);
        for (Usuario user: usuarios){
            if (user.getNombre().equals(nombre)
                    && user.getHashPassword().equals(hashIngresado)){
                return user;
            }
        }
        return null;
        }

    }

