package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DETALLE_COMPRA_MATERIA_PRIMA")
public class DetalleCompraMateriaPrima {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DET_COMPRA_MAT_PRIM")
    private Integer idDetCompraMatPrim;

    @Column(name = "CANTIDAD_MAT_PRIMA_COMPRADA", nullable = false)
    private Long cantidadMatPrimaComprada;

    @Column(name = "SUB_TOTAL_MAT_PRIMA_COMPRADA", nullable = false)
    private Long subTotalMatPrimaComprada;

    @Column(name = "ESTADO_DET_COMPRA_MAT_PRIMA", nullable = false)
    private Boolean estadoDetCompraMatPrima;

    // Relación ManyToOne con COMPRA (el maestro)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPRA_FK_DET_COMPRA_MAT_PRIMA", referencedColumnName = "ID_COMPRA", nullable = false)
    private Compra compra;

    // Relación ManyToOne con MATERIA_PRIMA (el ítem)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAT_PRIMA_FK_DET_COMPRA_MAT_PRIMA", referencedColumnName = "ID_MATERIA_PRIMA", nullable = false)
    private MateriaPrima materiaPrima;

    public DetalleCompraMateriaPrima() {}

    // --- Getters y Setters ---

    public Integer getIdDetCompraMatPrim() { return idDetCompraMatPrim; }
    public void setIdDetCompraMatPrim(Integer idDetCompraMatPrim) { this.idDetCompraMatPrim = idDetCompraMatPrim; }

    public Long getCantidadMatPrimaComprada() { return cantidadMatPrimaComprada; }
    public void setCantidadMatPrimaComprada(Long cantidadMatPrimaComprada) { this.cantidadMatPrimaComprada = cantidadMatPrimaComprada; }

    public Long getSubTotalMatPrimaComprada() { return subTotalMatPrimaComprada; }
    public void setSubTotalMatPrimaComprada(Long subTotalMatPrimaComprada) { this.subTotalMatPrimaComprada = subTotalMatPrimaComprada; }

    public Boolean getEstadoDetCompraMatPrima() { return estadoDetCompraMatPrima; }
    public void setEstadoDetCompraMatPrima(Boolean estadoDetCompraMatPrima) { this.estadoDetCompraMatPrima = estadoDetCompraMatPrima; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public MateriaPrima getMateriaPrima() { return materiaPrima; }
    public void setMateriaPrima(MateriaPrima materiaPrima) { this.materiaPrima = materiaPrima; }
}
