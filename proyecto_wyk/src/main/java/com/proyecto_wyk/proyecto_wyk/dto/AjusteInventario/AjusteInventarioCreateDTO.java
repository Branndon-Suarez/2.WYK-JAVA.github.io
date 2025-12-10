package com.proyecto_wyk.proyecto_wyk.dto.AjusteInventario;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AjusteInventarioCreateDTO {

    // --- Columna FECHA_AJUSTE ---
    @NotNull(message = "La fecha y hora del ajuste son *OBLIGATORIAS*.")
    private LocalDateTime fechaAjuste;

    // --- Columna TIPO_AJUSTE (ENUM) ---
    @NotBlank(message = "El tipo de ajuste es *OBLIGATORIO*.")
    @Pattern(
            regexp = "^(DAÑADO|ROBO|PERDIDA|CADUCADO|MUESTRA)$",
            message = "El tipo de ajuste debe ser uno de: DAÑADO, ROBO, PERDIDA, CADUCADO, o MUESTRA."
    )
    private String tipoAjuste;

    // --- Columna CANTIDAD_AJUSTADA ---
    @NotNull(message = "La cantidad ajustada es *OBLIGATORIA*.")
    @Min(value = 1, message = "La cantidad ajustada debe ser al menos 1.")
    @Max(value = 10000, message = "La cantidad ajustada no puede exceder 10000 unidades.")
    private Integer cantidadAjustada;

    // --- Columna DESCRIPCION ---
    @Size(
            max = 200,
            message = "La descripción no puede exceder los 200 carácteres."
    )
    private String descripcion;

    // --- ID_PROD_FK_AJUSTE_INVENTARIO ---
    @NotNull(message = "Debe especificar el ID del producto al que aplica el ajuste.")
    @Min(value = 1, message = "El ID del producto debe ser un número positivo.")
    private Long idProducto; // Mapea a BIGINT

    // --- ID_USUARIO_FK_AJUSTE_INVENTARIO ---
    @NotNull(message = "Debe especificar el ID del usuario que registra el ajuste.")
    @Min(value = 1, message = "El ID del usuario debe ser un número positivo.")
    private Integer idUsuario; // Mapea a INT

    // =================================================================
    // Getters y Setters
    // =================================================================

    public LocalDateTime getFechaAjuste() {
        return fechaAjuste;
    }

    public void setFechaAjuste(LocalDateTime fechaAjuste) {
        this.fechaAjuste = fechaAjuste;
    }

    public String getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(String tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public Integer getCantidadAjustada() {
        return cantidadAjustada;
    }

    public void setCantidadAjustada(Integer cantidadAjustada) {
        this.cantidadAjustada = cantidadAjustada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}