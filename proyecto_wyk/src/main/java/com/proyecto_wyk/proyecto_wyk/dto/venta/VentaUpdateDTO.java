package com.proyecto_wyk.proyecto_wyk.dto.venta;


import jakarta.validation.constraints.*;

public class VentaUpdateDTO {
    @NotNull(message = "El ID de la venta es *OBLIGATORIA*.")
    private Long idVenta;

    @Size(
            min = 1, max = 20,
            message = "El número de mesa no es válido."
    )
    @Pattern(
            regexp = "^[0-9]+$",
            message = "El número de mesa solo puede contener números."
    )
    private String numeroMesa; // Nullable

    @Size(
            max = 200,
            message = "La descripción debe tener máximo 200 carácteres."
    )
    private String descripcion;

    @NotEmpty(message = "Debe seleccionar un estado de pedido.")
    private String estadoPedido;

    @NotEmpty(message = "Debe seleccionar un estado de pago.")
    private String estadoPago;

    // Getters y Setters
    public Long getIdVenta() {
        return idVenta;
    }
    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public String getNumeroMesa() {
        return numeroMesa;
    }
    public void setNumeroMesa(String numeroMesa) {
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
