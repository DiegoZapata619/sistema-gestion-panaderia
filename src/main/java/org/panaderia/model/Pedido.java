package org.panaderia.model;

import java.util.Date;

public class Pedido {
    String idVenta;
    Date fecha;
    double total;
    MetodoPago metodoPago;

    public Pedido (String idVenta, Date fecha, double total, MetodoPago metodoPago){
        this.idVenta=idVenta;
        this.fecha=fecha;
        this.total=total;
        this.metodoPago=metodoPago;
    }
    public Pedido(){

    }


}
