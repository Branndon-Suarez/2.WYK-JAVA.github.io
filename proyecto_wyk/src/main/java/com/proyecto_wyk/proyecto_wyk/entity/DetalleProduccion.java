package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DETALLE_PRODUCCION")
public class DetalleProduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE_PRODUCCION")
    private Long idDetalleProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUCCION_FK_DET_PRODUC", nullable = false)
    private Produccion produccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MATERIA_PRIMA_FK_DET_PRODUC", nullable = false)
    private MateriaPrima materiaPrima;

    @Column(name = "CANTIDAD_REQUERIDA", nullable = false)
    private Double cantidadRequerida;

    // Getters y Setters


    public Long getIdDetalleProduccion() {
        return idDetalleProduccion;
    }
    public void setIdDetalleProduccion(Long idDetalleProduccion) {
        this.idDetalleProduccion = idDetalleProduccion;
    }

    public Produccion getProduccion() {
        return produccion;
    }
    public void setProduccion(Produccion produccion) {
        this.produccion = produccion;
    }

    public MateriaPrima getMateriaPrima() {
        return materiaPrima;
    }
    public void setMateriaPrima(MateriaPrima materiaPrima) {
        this.materiaPrima = materiaPrima;
    }

    public Double getCantidadRequerida() {
        return cantidadRequerida;
    }
    public void setCantidadRequerida(Double cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }
}
