package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "PRODUCTO")
public class Producto {
    @Id
    @Column(name = "ID_PRODUCTO")
    private Long idProducto; // Mapeado a BIGINT

    @Column(name = "NOMBRE_PRODUCTO", nullable = false, length = 50)
    private String nombreProducto;

    @Column(name = "VALOR_UNITARIO_PRODUCTO", nullable = false)
    private Long valorUnitarioProducto; // Mapeado a BIGINT

    @Column(name = "CANT_EXIST_PRODUCTO", nullable = false)
    private Long cantExistProducto; // Mapeado a BIGINT (Stock)

    @Column(name = "FECHA_VENCIMIENTO_PRODUCTO", nullable = false)
    private LocalDate fechaVencimientoProducto;

    @Column(name = "TIPO_PRODUCTO", nullable = false, columnDefinition = "ENUM('PANADERIA', 'PASTELERIA', 'ASEO')")
    private String tipoProducto;

    // Relación ManyToOne con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_FK_PRODUCTO", nullable = false)
    private Usuario usuario;

    @Column(name = "ESTADO_PRODUCTO", nullable = false)
    private Boolean estadoProducto;

    // Constructor vacío
    public Producto() {
    }

    // Getters y Setters

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

    public Long getValorUnitarioProducto() {
        return valorUnitarioProducto;
    }
    public void setValorUnitarioProducto(Long valorUnitarioProducto) {
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

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getEstadoProducto() {
        return estadoProducto;
    }
    public void setEstadoProducto(Boolean estadoProducto) {
        this.estadoProducto = estadoProducto;
    }
}
