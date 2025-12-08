package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DETALLE_COMPRA_PRODUCTO")
public class DetalleCompraProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DET_COMPRA_PROD")
    private Integer idDetCompraProd;

    @Column(name = "CANTIDAD_PROD_COMPRADO", nullable = false)
    private Long cantidadProdComprado;

    @Column(name = "SUB_TOTAL_PROD_COMPRADO", nullable = false)
    private Long subTotalProdComprado;

    @Column(name = "ESTADO_DET_COMPRA_PROD", nullable = false)
    private Boolean estadoDetCompraProd;

    // Relación ManyToOne con COMPRA (el maestro)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPRA_FK_DET_COMPRA_PROD", referencedColumnName = "ID_COMPRA", nullable = false)
    private Compra compra;

    // Relación ManyToOne con PRODUCTO (el ítem)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROD_FK_DET_COMPRA_PROD", referencedColumnName = "ID_PRODUCTO", nullable = false)
    private Producto producto;

    public DetalleCompraProducto() {}

    // --- Getters y Setters ---

    public Integer getIdDetCompraProd() { return idDetCompraProd; }
    public void setIdDetCompraProd(Integer idDetCompraProd) { this.idDetCompraProd = idDetCompraProd; }

    public Long getCantidadProdComprado() { return cantidadProdComprado; }
    public void setCantidadProdComprado(Long cantidadProdComprado) { this.cantidadProdComprado = cantidadProdComprado; }

    public Long getSubTotalProdComprado() { return subTotalProdComprado; }
    public void setSubTotalProdComprado(Long subTotalProdComprado) { this.subTotalProdComprado = subTotalProdComprado; }

    public Boolean getEstadoDetCompraProd() { return estadoDetCompraProd; }
    public void setEstadoDetCompraProd(Boolean estadoDetCompraProd) { this.estadoDetCompraProd = estadoDetCompraProd; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
