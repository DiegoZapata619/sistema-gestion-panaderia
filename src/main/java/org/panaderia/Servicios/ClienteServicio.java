package org.panaderia.Servicios;

import org.panaderia.model.Cliente;

public class ClienteServicio {

    public void registrarCompra(Cliente cliente) {
        cliente.setVisitas(cliente.getVisitas() + 1);
        cliente.setPuntos(cliente.getPuntos() + 10);
    }

    public boolean esFrecuente(Cliente cliente) {
        return cliente.getVisitas() >= 5;
    }

    public double aplicarDescuento(Cliente cliente, double total) {

        // Descuento por puntos
        if (cliente.getPuntos() >= 100) {
            cliente.setPuntos(cliente.getPuntos() - 100);
            return total * 0.9;
        }

        // Descuento por cliente frecuente
        if (esFrecuente(cliente)) {
            return total * 0.95;
        }

        return total;
    }

    public String tipoCliente(Cliente cliente) {
        return esFrecuente(cliente) ? "Frecuente" : "Normal";
    }
}
