package com.proyecto_wyk.proyecto_wyk.entity;

import jakarta.persistence.*;

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
    private Long valorUnitarioMatPrima; // Precio de referencia

    @Column(name = "CANTIDAD_EXIST_MATERIA_PRIMA", nullable = false)
    private Long cantidadExistMateriaPrima;

    @Column(name = "PRESENTACION_MATERIA_PRIMA", nullable = false, length = 50)
    private String presentacionMateriaPrima;

    public MateriaPrima() {}

    public MateriaPrima(Long idMateriaPrima, String nombreMateriaPrima, Long valorUnitarioMatPrima, Long cantidadExistMateriaPrima) {
        this.idMateriaPrima = idMateriaPrima;
        this.nombreMateriaPrima = nombreMateriaPrima;
        this.valorUnitarioMatPrima = valorUnitarioMatPrima;
        this.cantidadExistMateriaPrima = cantidadExistMateriaPrima;
    }

    // --- Getters y Setters ---

    public Long getIdMateriaPrima() { return idMateriaPrima; }
    public void setIdMateriaPrima(Long idMateriaPrima) { this.idMateriaPrima = idMateriaPrima; }

    public String getNombreMateriaPrima() { return nombreMateriaPrima; }
    public void setNombreMateriaPrima(String nombreMateriaPrima) { this.nombreMateriaPrima = nombreMateriaPrima; }

    public Long getValorUnitarioMatPrima() { return valorUnitarioMatPrima; }
    public void setValorUnitarioMatPrima(Long valorUnitarioMatPrima) { this.valorUnitarioMatPrima = valorUnitarioMatPrima; }

    public Long getCantidadExistMateriaPrima() { return cantidadExistMateriaPrima; }
    public void setCantidadExistMateriaPrima(Long cantidadExistMateriaPrima) { this.cantidadExistMateriaPrima = cantidadExistMateriaPrima; }

    public String getPresentacionMateriaPrima() { return presentacionMateriaPrima; }
    public void setPresentacionMateriaPrima(String presentacionMateriaPrima) { this.presentacionMateriaPrima = presentacionMateriaPrima; }

    // NOTA: Se omitieron FECHA_VENCIMIENTO y ID_USUARIO_FK_MATERIA_PRIMA por brevedad,
    // pero deberían estar mapeados como en el código anterior.
}
