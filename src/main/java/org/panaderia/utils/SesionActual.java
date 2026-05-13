package org.panaderia.utils;

import org.panaderia.model.Usuario;

/**
 * Utilidad para manejar la sesión del usuario actual en la aplicación.
 * Permite compartir el usuario autenticado entre diferentes controladores.
 */
public class SesionActual {
    private static Usuario usuarioActual;
    
    /**
     * Establece el usuario autenticado actualmente en sesión.
     * @param usuario El usuario que ha iniciado sesión
     */
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
    
    /**
     * Obtiene el usuario actualmente autenticado.
     * @return El usuario en sesión, o null si no hay sesión activa
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Cierra la sesión actual.
     */
    public static void cerrarSesion() {
        usuarioActual = null;
    }
    
    /**
     * Verifica si hay una sesión activa.
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
}
