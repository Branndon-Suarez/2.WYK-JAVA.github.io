package com.proyecto_wyk.proyecto_wyk.dto.producto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ProductoUpdateDTO {
    @NotNull(message = "El ID del producto es *OBLIGATORIO*.")
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es *OBLIGATORIO*")
    @Size(
            min = 3, max = 50,
            message = "El nombre debe de tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre solo puede contener letras."
    )
    private String nombreProducto;

    @NotBlank(message = "El precio unitario es *OBLIGATORIO*")
    @Pattern(
            regexp = "^[0-9]+$",
            message = "El precio unitario solo puede contener números."
    )
    private String valorUnitarioProducto;

    @NotNull(message = "La cantidad de existencia es *OBLIGATORIA*") // Si es Long, debe ser @NotNull
    @Min(value = 0, message = "La cantidad no puede ser negativa.")
    private Long cantExistProducto;

    @NotNull(message = "La fecha de vencimiento es *OBLIGATORIA* para el inventario.")
    @FutureOrPresent(message = "La fecha de vencimiento no puede ser una fecha pasada.")
    private LocalDate fechaVencimientoProducto;

    @NotEmpty(message = "Debe seleccionar un tipo de producto.")
    private String tipoProducto;

    @NotNull(message = "Debe seleccionar el estado.")
    private boolean estadoProducto;

    // Getters y setters

    public Long getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getValorUnitarioProducto() {
        return valorUnitarioProducto;
    }
    public void setValorUnitarioProducto(String valorUnitarioProducto) {
        this.valorUnitarioProducto = valorUnitarioProducto;
    }

    public Long getCantExistProducto() {
        return cantExistProducto;
    }
    public void setCantExistProducto(Long cantExistProducto) {
        this.cantExistProducto = cantExistProducto;
    }

    public LocalDate getFechaVencimientoProducto() {
        return fechaVencimientoProducto;
    }
    public void setFechaVencimientoProducto(LocalDate fechaVencimientoProducto) {
        this.fechaVencimientoProducto = fechaVencimientoProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }
    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public boolean isEstadoProducto() {
        return estadoProducto;
    }
    public void setEstadoProducto(boolean estadoProducto) {
        this.estadoProducto = estadoProducto;
    }
}
