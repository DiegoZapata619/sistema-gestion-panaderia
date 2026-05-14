package org.panaderia.model;

/// Clase apoyo para poder obtener el usuario actual de la Sesión
///Útil para la vista de empleado, en donde venta requiere de un empleado asociado para concretarse
public class Sesion {
    private static Usuario usuarioActual;
    /// Constructor private. únicamente Requerimos una instancia de esta clase.
    private Sesion (){
    }
    public static void iniciar (Usuario usuario){
        usuarioActual=usuario;
    }

    public static void cerrar(){
        usuarioActual=null;
    }

    public static Usuario getUsuario (){
        return usuarioActual;
    }
    public static Empleado getEmpleado() {
        if (usuarioActual instanceof Empleado empleado) {
            return empleado;
        }
        return null;
    }
    public static boolean haySesion (){
        return usuarioActual!=null;
    }
}
