package com.proyecto_wyk.proyecto_wyk.dto.produccion;

import java.util.List;

public class ProduccionDTO {
    private String nombre;
    private Long cantidadAProducir;
    private String descripcion;
    private Long productoId; // ID del producto terminado
    private Long usuarioId;
    private List<DetalleProduccionDTO> insumos;

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCantidadAProducir() {
        return cantidadAProducir;
    }
    public void setCantidadAProducir(Long cantidadAProducir) {
        this.cantidadAProducir = cantidadAProducir;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getProductoId() {
        return productoId;
    }
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<DetalleProduccionDTO> getInsumos() {
        return insumos;
    }
    public void setInsumos(List<DetalleProduccionDTO> insumos) {
        this.insumos = insumos;
    }
}
