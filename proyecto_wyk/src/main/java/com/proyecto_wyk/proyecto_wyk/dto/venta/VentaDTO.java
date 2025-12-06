package com.proyecto_wyk.proyecto_wyk.dto.venta;

import java.util.List;

public class VentaDTO {
    private String fecha; // Enviado como String "YYYY-MM-DDTmm:ss" desde JS
    private Integer mesa;
    private String estadoPedido;
    private String estadoPago;
    private String descripcion;
    private Long usuarioId; // ID_USUARIO_FK_VENTA
    private Double total; // Total General de la Venta
    private List<DetalleVentaDTO> productos; // La lista de detalles

    // Constructor vacío
    public VentaDTO() {
    }

    // Getters y Setters

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getMesa() {
        return mesa;
    }
    public void setMesa(Integer mesa) {
        this.mesa = mesa;
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

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }

    public List<DetalleVentaDTO> getProductos() {
        return productos;
    }
    public void setProductos(List<DetalleVentaDTO> productos) {
        this.productos = productos;
    }
}
