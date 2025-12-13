package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "MATERIA_PRIMA")
public class MateriaPrima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MATERIA_PRIMA")
    private Long idMateriaPrima;

    @Column(name = "NOMBRE_MATERIA_PRIMA", nullable = false, length = 50)
    private String nombreMateriaPrima;

    @Column(name = "VALOR_UNITARIO_MAT_PRIMA", nullable = false)
    private Long valorUnitarioMatPrima;

    @Column(name = "FECHA_VENCIMIENTO_MATERIA_PRIMA", nullable = false)
    private LocalDate FechaVencimientoMateriaPrima;

    @Column(name = "CANTIDAD_EXIST_MATERIA_PRIMA", nullable = false)
    private Long cantidadExistMateriaPrima;

    @Column(name = "PRESENTACION_MATERIA_PRIMA", nullable = false, length = 50)
    private String presentacionMateriaPrima;

    @Column(name = "DESCRIPCION_MATERIA_PRIMA", nullable = false, length = 200)
    private String descripcionMateriaPrima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_FK_MATERIA_PRIMA", nullable = false)
    private Usuario usuario;

    @Column(name = "ESTADO_MATERIA_PRIMA", nullable = false)
    private Boolean estadoMateriaPrima;

    public MateriaPrima() {}

    // --- Getters y Setters ---

    public Long getIdMateriaPrima() { return idMateriaPrima; }
    public void setIdMateriaPrima(Long idMateriaPrima) { this.idMateriaPrima = idMateriaPrima; }

    public String getNombreMateriaPrima() { return nombreMateriaPrima; }
    public void setNombreMateriaPrima(String nombreMateriaPrima) { this.nombreMateriaPrima = nombreMateriaPrima; }

    public Long getValorUnitarioMatPrima() { return valorUnitarioMatPrima; }
    public void setValorUnitarioMatPrima(Long valorUnitarioMatPrima) { this.valorUnitarioMatPrima = valorUnitarioMatPrima; }

    public LocalDate getFechaVencimientoMateriaPrima() {
        return FechaVencimientoMateriaPrima;
    }
    public void setFechaVencimientoMateriaPrima(LocalDate fechaVencimientoMateriaPrima) {
        FechaVencimientoMateriaPrima = fechaVencimientoMateriaPrima;
    }

    public Long getCantidadExistMateriaPrima() { return cantidadExistMateriaPrima; }
    public void setCantidadExistMateriaPrima(Long cantidadExistMateriaPrima) { this.cantidadExistMateriaPrima = cantidadExistMateriaPrima; }

    public String getPresentacionMateriaPrima() { return presentacionMateriaPrima; }
    public void setPresentacionMateriaPrima(String presentacionMateriaPrima) { this.presentacionMateriaPrima = presentacionMateriaPrima; }

    public String getDescripcionMateriaPrima() {
        return descripcionMateriaPrima;
    }
    public void setDescripcionMateriaPrima(String descripcionMateriaPrima) {
        this.descripcionMateriaPrima = descripcionMateriaPrima;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getEstadoMateriaPrima() {
        return estadoMateriaPrima;
    }
    public void setEstadoMateriaPrima(Boolean estadoMateriaPrima) {
        this.estadoMateriaPrima = estadoMateriaPrima;
    }
}
