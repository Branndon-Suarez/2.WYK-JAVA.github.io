package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AJUSTE_INVENTARIO")
public class AjusteInventario {

    // --- Columna ID_AJUSTE (Clave Primaria, Auto Incremento) ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AJUSTE", nullable = false)
    private Integer idAjuste;

    // --- Columna FECHA_AJUSTE (DATETIME NOT NULL) ---
    @Column(name = "FECHA_AJUSTE", nullable = false)
    private LocalDateTime fechaAjuste;

    // --- Columna TIPO_AJUSTE (ENUM NOT NULL) ---
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_AJUSTE", nullable = false, length = 10)
    private TipoAjuste tipoAjuste;

    // --- Columna CANTIDAD_AJUSTADA (INT NOT NULL) ---
    @Column(name = "CANTIDAD_AJUSTADA", nullable = false)
    private Integer cantidadAjustada;

    // --- Columna DESCRIPCION (VARCHAR(200) NULL) ---
    @Column(name = "DESCRIPCION", length = 200, nullable = true)
    private String descripcion;

    // --- Clave Foránea a PRODUCTO ---
    @ManyToOne
    @JoinColumn(name = "ID_PROD_FK_AJUSTE_INVENTARIO", referencedColumnName = "ID_PRODUCTO", nullable = false)
    private Producto producto; // Asume la entidad Producto

    // --- Clave Foránea a USUARIO ---
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_FK_AJUSTE_INVENTARIO", referencedColumnName = "ID_USUARIO", nullable = false)
    private Usuario usuario; // Asume la entidad Usuario

    // =================================================================
    // ENUM: 'DAÑADO', 'ROBO', 'PERDIDA', 'CADUCADO', 'MUESTRA'
    // =================================================================
    public enum TipoAjuste {
        DAÑADO,
        ROBO,
        PERDIDA,
        CADUCADO,
        MUESTRA
    }

    // --- Constructor, Getters y Setters ---

    public AjusteInventario() {
    }

    public Integer getIdAjuste() {
        return idAjuste;
    }

    public void setIdAjuste(Integer idAjuste) {
        this.idAjuste = idAjuste;
    }

    public LocalDateTime getFechaAjuste() {
        return fechaAjuste;
    }

    public void setFechaAjuste(LocalDateTime fechaAjuste) {
        this.fechaAjuste = fechaAjuste;
    }

    public TipoAjuste getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjuste tipoAjuste) {
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