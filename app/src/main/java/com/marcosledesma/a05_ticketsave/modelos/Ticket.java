package com.marcosledesma.a05_ticketsave.modelos;

import java.util.Date;

public class Ticket {
    private String urlImagen;
    private String nombreComercio;
    private Date fechaCompra;
    private Float importeCompra;

    // Const vacío para FireBase (convertir json a objeto)
    public Ticket() {
    }

    // Const completo
    public Ticket(String urlImagen, String nombreComercio, Date fechaCompra, Float importeCompra) {
        this.urlImagen = urlImagen;
        this.nombreComercio = nombreComercio;
        this.fechaCompra = fechaCompra;
        this.importeCompra = importeCompra;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Float getImporteCompra() {
        return importeCompra;
    }

    public void setImporteCompra(Float importeCompra) {
        this.importeCompra = importeCompra;
    }
}
