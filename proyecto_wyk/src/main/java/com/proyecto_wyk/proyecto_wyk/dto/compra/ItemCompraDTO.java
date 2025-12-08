package com.proyecto_wyk.proyecto_wyk.dto.compra;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemCompraDTO {
    @NotNull(message = "El ID del ítem es obligatorio")
    private Long id; // ID_MATERIA_PRIMA o ID_PRODUCTO

    @NotNull(message = "El tipo de ítem es obligatorio")
    private String tipo; // MP o PROD

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Long cantidad;

    @Positive(message = "El precio unitario debe ser positivo")
    private Long precio_unitario;

    @Positive(message = "El subtotal debe ser positivo")
    private Long subtotal;

    // --- Getters y Setters (Necesarios para el mapeo JSON) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getCantidad() { return cantidad; }
    public void setCantidad(Long cantidad) { this.cantidad = cantidad; }

    public Long getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(Long precio_unitario) { this.precio_unitario = precio_unitario; }

    public Long getSubtotal() { return subtotal; }
    public void setSubtotal(Long subtotal) { this.subtotal = subtotal; }
}
