package com.proyecto_wyk.proyecto_wyk.dto.produccion;

public class DetalleProduccionDTO {
    private Long materiaPrimaId;
    private Double cantidadRequerida;

    // Getters y Setters

    public Long getMateriaPrimaId() {
        return materiaPrimaId;
    }
    public void setMateriaPrimaId(Long materiaPrimaId) {
        this.materiaPrimaId = materiaPrimaId;
    }

    public Double getCantidadRequerida() {
        return cantidadRequerida;
    }
    public void setCantidadRequerida(Double cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }
}
