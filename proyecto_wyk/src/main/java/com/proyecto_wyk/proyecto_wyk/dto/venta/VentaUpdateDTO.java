package com.proyecto_wyk.proyecto_wyk.dto.venta;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VentaUpdateDTO {
    @NotNull(message = "El ID del rol es obligatorio.")
    private Long idVenta;


    private Integer numeroMesa; // Nullable

    @NotNull(message = "El nombre del rol es obligatorio.")
    @Size(
            max = 50,
            message = "El rol debe tener entre 3 y 50 carácteres."
    )
    private String descripcion;

    private String estadoPedido;

    private String estadoPago;

    // Getters y Setters
    public Long getIdVenta() {
        return idVenta;
    }
    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }
    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }
    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getEstadoPago() {
        return estadoPago;
    }
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}
