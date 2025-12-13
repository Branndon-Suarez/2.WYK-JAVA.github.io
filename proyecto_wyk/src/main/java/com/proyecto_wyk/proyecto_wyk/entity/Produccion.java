package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCCION")
public class Produccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUCCION")
    private Long idProduccion;

    @Column(name = "NOMBRE_PRODUCCION", nullable = false, length = 50)
    private String nombreProduccion;

    @Column(name = "CANT_PRODUCCION", nullable = false)
    private Long cantProduccion;

    @Column(name = "DESCRIPCION_PRODUCCION", nullable = false, length = 200)
    private String descripcionProduccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_PRODUCCION", nullable = false)
    private EstadoProduccion estadoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUCTO_FK_PRODUCCION", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_FK_PRODUCCION", nullable = false)
    private Usuario usuario;

    public enum EstadoProduccion {
        PENDIENTE, EN_PROCESO, FINALIZADA, CANCELADA
    }

    // Getters y Setters

    public Long getIdProduccion() {
        return idProduccion;
    }
    public void setIdProduccion(Long idProduccion) {
        this.idProduccion = idProduccion;
    }
    public String getNombreProduccion() {
        return nombreProduccion;
    }
    public void setNombreProduccion(String nombreProduccion) {
        this.nombreProduccion = nombreProduccion;
    }

    public Long getCantProduccion() {
        return cantProduccion;
    }
    public void setCantProduccion(Long cantProduccion) {
        this.cantProduccion = cantProduccion;
    }

    public String getDescripcionProduccion() {
        return descripcionProduccion;
    }
    public void setDescripcionProduccion(String descripcionProduccion) {
        this.descripcionProduccion = descripcionProduccion;
    }

    public EstadoProduccion getEstadoProduccion() {
        return estadoProduccion;
    }
    public void setEstadoProduccion(EstadoProduccion estadoProduccion) {
        this.estadoProduccion = estadoProduccion;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
