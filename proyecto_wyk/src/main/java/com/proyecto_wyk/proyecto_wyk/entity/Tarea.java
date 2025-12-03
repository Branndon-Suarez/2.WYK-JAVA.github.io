package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TAREA")
public class Tarea {

    // --- Columna ID_TAREA ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TAREA", nullable = false)
    private Long idTarea;

    // --- Columna TAREA ---
    @Column(name = "TAREA", length = 100, nullable = false)
    private String tarea;

    // --- Columna CATEGORIA ---
    @Column(name = "CATEGORIA", length = 80, nullable = false)
    private String categoria;

    // --- Columna DESCRIPCION ---
    @Column(name = "DESCRIPCION", length = 250, nullable = true) // NULL en la BD
    private String descripcion;

    // --- Columna TIEMPO_ESTIMADO_HORAS ---
    @Column(name = "TIEMPO_ESTIMADO_HORAS", nullable = false)
    private Float tiempoEstimadoHoras;

    // --- Columna PRIORIDAD (ENUM) ---
    @Enumerated(EnumType.STRING) // Mapea el Enum de Java al ENUM de la BD como String
    @Column(name = "PRIORIDAD", nullable = false)
    private Prioridad prioridad; // Definir el enum

    // --- Columna ESTADO_TAREA (ENUM) ---
    @Enumerated(EnumType.STRING) // Mapea el Enum de Java al ENUM de la BD como String
    @Column(name = "ESTADO_TAREA", nullable = false)
    private EstadoTarea estadoTarea; // Definir el enum

    // --- Columna USUARIO_ASIGNADO_FK (Clave Foránea a USUARIO) ---
    // MUCHAS tareas pueden estar asignadas a UN solo usuario.
    @ManyToOne
    @JoinColumn(name = "USUARIO_ASIGNADO_FK", nullable = false)
    private Usuario usuarioAsignado; // Referencia a la entidad Usuario

    // --- Columna USUARIO_CREADOR_FK (Clave Foránea a USUARIO) ---
    // MUCHAS tareas pueden ser creadas por UN solo usuario.
    @ManyToOne
    @JoinColumn(name = "USUARIO_CREADOR_FK", nullable = false)
    private Usuario usuarioCreador; // Referencia a la entidad Usuario

    // Enum interno para reflejar el ENUM de MySQL
    //    Enum de prioridad
    public enum Prioridad {
        BAJA, MEDIA, ALTA
    }

    //    Enum de Estado Tarea
    public enum EstadoTarea {
        PENDIENTE, COMPLETADA, CANCELADA
    }

    // --- Constructor, Getters y Setters ---

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

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoTarea getEstadoTarea() {
        return estadoTarea;
    }

    public void setEstadoTarea(EstadoTarea estadoTarea) {
        this.estadoTarea = estadoTarea;
    }

    public Usuario getUsuarioAsignado() {
        return usuarioAsignado;
    }

    public void setUsuarioAsignado(Usuario usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }
}
