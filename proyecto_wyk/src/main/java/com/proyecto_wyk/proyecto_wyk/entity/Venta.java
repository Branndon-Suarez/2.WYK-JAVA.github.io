package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VENTA")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VENTA")
    private Long idVenta;

    @Column(name = "FECHA_HORA_VENTA", nullable = false)
    private LocalDateTime fechaHoraVenta;

    @Column(name = "NUMERO_MESA")
    private Integer numeroMesa; // Nullable

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @Column(name = "ESTADO_PEDIDO", length = 50, nullable = false)
    private String estadoPedido;

    @Column(name = "ESTADO_PAGO", length = 50, nullable = false)
    private String estadoPago;

    @Column(name = "TOTAL_VENTA", nullable = false)
    private Double totalVenta;

    // Relación ManyToOne con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_FK_VENTA", nullable = false)
    private Usuario usuario; // Asumo que tienes la entidad Usuario

    // Constructor vacío
    public Venta() {
    }

    // Getters y setters
    public Long getIdVenta() {
        return idVenta;
    }
    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFechaHoraVenta() {
        return fechaHoraVenta;
    }
    public void setFechaHoraVenta(LocalDateTime fechaHoraVenta) {
        this.fechaHoraVenta = fechaHoraVenta;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }
    public void setNumeroMesa(Integer numeroMesa) {
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

    public Double getTotalVenta() {
        return totalVenta;
    }
    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
