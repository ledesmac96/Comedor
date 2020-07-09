package com.unse.bienestar.comedor.Modelo;

import org.json.JSONException;
import org.json.JSONObject;

public class ReservaEspecial extends Reserva {

    int porcion;
    String nombre;
    String descripcionEspecial;


    public ReservaEspecial(int idReserva, int idMenu, int validez, String fechaReserva, String nombre,
                           String apellido, String descripcion, String tipoEntrega, int porcion,
                           String descripcionEspecial) {
        super(idReserva, idMenu, validez, fechaReserva, nombre, apellido, descripcion, tipoEntrega);
        this.porcion = porcion;
        this.nombre = nombre;
        this.descripcionEspecial = descripcionEspecial;
    }

    @Override
    public int getPorcion() {
        return porcion;
    }

    @Override
    public void setPorcion(int porcion) {
        this.porcion = porcion;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionEspecial() {
        return descripcionEspecial;
    }

    public void setDescripcionEspecial(String descripcionEspecial) {
        this.descripcionEspecial = descripcionEspecial;
    }

    public static ReservaEspecial mapper(JSONObject object, int tipo) {
        int idReserva, idUsuario, idMenu, validez, porcion;
        String fechaReserva, fechaModificacion, nombre, estadoDescripcion, estado;
        ReservaEspecial reserva = null;
        try {
            switch (tipo) {
                case MEDIUM:
                    idReserva = Integer.parseInt(object.getString("idreservaespecial"));
                    //idUsuario = Integer.parseInt(object.getString("idusuario"));
                    //idMenu = Integer.parseInt(object.getString("idmenu"));
                    porcion = Integer.parseInt(object.getString("porcion"));
                    estado = object.getString("estadodescripcion");
                    fechaReserva = object.getString("fechareserva");
                    estadoDescripcion = object.getString("descripcion");
                    reserva = new ReservaEspecial(idReserva, 0, 0, fechaReserva,
                            "", "", estado,
                            "", porcion, estadoDescripcion);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reserva;

    }
}
