package com.proyecto_wyk.proyecto_wyk.dto.venta;

public class DetalleVentaDTO {
    private Long id; // ID del producto (ID_PRODUCTO_FK_DET_VENTA)
    private Long cantidad;
    private Double precio; // Precio Unitario (VALOR_UNITARIO_PRODUCTO)

    // Constructor vacío
    public DetalleVentaDTO() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCantidad() {
        return cantidad;
    }
    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
