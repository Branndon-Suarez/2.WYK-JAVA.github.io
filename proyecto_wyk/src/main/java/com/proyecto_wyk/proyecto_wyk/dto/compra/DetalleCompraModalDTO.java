package com.proyecto_wyk.proyecto_wyk.dto.compra;

import lombok.Data;

public class DetalleCompraModalDTO {
    // Para la columna "Tipo" (MP o PROD)
    private String tipo;

    // Para la columna "Item"
    private String itemNombre;

    // Para las columnas de valores
    private Integer cantidad;
    private Double precioUnitario;
    private Double subTotal;

    public DetalleCompraModalDTO(String tipo, String itemNombre, Integer cantidad, Double precioUnitario, Double subTotal) {
        this.tipo = tipo;
        this.itemNombre = itemNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subTotal = subTotal;
    }

    // Getters y Setters

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getItemNombre() {
        return itemNombre;
    }
    public void setItemNombre(String itemNombre) {
        this.itemNombre = itemNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }
}
