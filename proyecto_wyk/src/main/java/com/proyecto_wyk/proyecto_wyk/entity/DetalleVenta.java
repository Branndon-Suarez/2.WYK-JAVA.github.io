package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DETALLE_VENTA")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_VENTA")
    private Long idDetalleVenta;

    @Column(name = "CANTIDAD", nullable = false)
    private Long cantidad;

    @Column(name = "SUB_TOTAL", nullable = false)
    private Double subTotal;

    // Relación ManyToOne con Venta (Maestro)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VENTA_FK_DET_VENTA", nullable = false)
    private Venta venta;

    // Relación ManyToOne con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUCTO_FK_DET_VENTA", nullable = false)
    private Producto producto; // Asumo que tienes la entidad Producto

    // Constructor vacío
    public DetalleVenta() {
    }

    // ... Getters y Setters
    public Long getIdDetalleVenta() {
        return idDetalleVenta;
    }
    public void setIdDetalleVenta(Long idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public Long getCantidad() {
        return cantidad;
    }
    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Venta getVenta() {
        return venta;
    }
    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
