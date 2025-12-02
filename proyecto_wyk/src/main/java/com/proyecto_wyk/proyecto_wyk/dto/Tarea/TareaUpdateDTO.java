package com.proyecto_wyk.proyecto_wyk.dto.Tarea;

import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import jakarta.validation.constraints.*;

public class TareaUpdateDTO {

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long idTarea;

    @NotBlank(message = "El nombre de la tarea es *OBLIGATORIO*")
    @Size(
            min = 3, max = 100,
            message = "El nombre debe de tener entre 3 y 100 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre solo puede contener letras."
    )
    private String tarea;

    // --- Columna CATEGORIA ---
    @NotBlank(message = "La categoria es *OBLIGATORIO*")
    @Size(
            min = 3, max = 80,
            message = "La categoría debe de tener entre 3 y 80 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "La categoría solo puede contener letras."
    )
    private String categoria;

    // --- Columna DESCRIPCION ---
    @Size(
            min = 3, max = 250,
            message = "La descripción debe de tener entre 3 y 80 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "La descripción solo puede contener letras."
    )
    private String descripcion;

    // --- Columna TIEMPO_ESTIMADO_HORAS ---
    @NotNull(message = "El tiempo estimado es *OBLIGATORIO*.")
    // Valor mínimo: 0.1 (inclusive)
    @DecimalMin(value = "0.1", inclusive = true, message = "El tiempo estimado debe ser mínimo 0.1 horas.")
    // Valor máximo: 10.0 (inclusive)
    @DecimalMax(value = "10.0", inclusive = true, message = "El tiempo estimado no puede exceder las 10 horas.")
    private Float tiempoEstimadoHoras;

    // --- Columna PRIORIDAD (ENUM) ---
    @NotNull(message = "Debe seleccionar una prioridad.")
    private Tarea.Prioridad prioridad; // Definir el enum

    // --- Columna ESTADO TAREA (ENUM) ---
    @NotNull(message = "Debe seleccionar un estado de la tarea.")
    private Tarea.EstadoTarea estadoTarea;


    // --- Columna USUARIO_ASIGNADO_FK (Clave Foránea a USUARIO) ---
    @NotNull(message = "Debe seleccionar mínimo un usuario a asignar")
    private Integer idUsuarioAsignado;

    // ---  Getters y Setters ---
    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getTiempoEstimadoHoras() {
        return tiempoEstimadoHoras;
    }

    public void setTiempoEstimadoHoras(Float tiempoEstimadoHoras) {
        this.tiempoEstimadoHoras = tiempoEstimadoHoras;
    }

    public Tarea.Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Tarea.Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Tarea.EstadoTarea getEstadoTarea() {
        return estadoTarea;
    }

    public void setEstadoTarea(Tarea.EstadoTarea estadoTarea) {
        this.estadoTarea = estadoTarea;
    }

    public Integer getIdUsuarioAsignado() {
        return idUsuarioAsignado;
    }

    public void setIdUsuarioAsignado(Integer idUsuarioAsignado) {
        this.idUsuarioAsignado = idUsuarioAsignado;
    }
}
