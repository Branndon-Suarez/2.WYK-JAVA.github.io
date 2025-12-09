package com.proyecto_wyk.proyecto_wyk.dto.compra;

import com.proyecto_wyk.proyecto_wyk.entity.Compra;
import jakarta.validation.constraints.*;

public class CompraUpdateDTO {
    @NotNull(message = "El ID de la venta es *OBLIGATORIA*.")
    private Long idCompra;

    @NotBlank(message = "El nombre es *OBLIGATORIO*.")
    @Size(
            min = 3, max = 50,
            message = "El nombre debe de tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre solo puede contener letras."
    )
    private String nombreProveedor;

    @NotBlank(message = "La marca es *OBLIGATORIA*.")
    @Size(
            min = 3, max = 50,
            message = "La marca debe de tener entre 3 y 50 carácteres."
    )
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "La marca solo puede contener letras."
    )
    private String marca;

    @NotBlank(message = "El teléfono es *OBLIGATORIO*.")
    @Size(
            min = 10, max = 10,
            message = "El teléfono debe tener exactamente 10 dígitos."
    )
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "El teléfono solo puede contener números y debe tener 10 dígitos."
    )
    private String telProveedor;

    @NotBlank(message = "El gmail es *OBLIGATORIO*.")
    @Email(message = "Debe digitar un gmail válido.")
    @Size(max = 50, message = "El email no debe exceder los 50 caracteres.")
    private String emailProveedor;

    @Size(
            max = 200,
            message = "La descripción debe tener máximo 200 carácteres."
    )
    private String descripcionCompra;

    @NotEmpty(message = "Debe seleccionar un estado de pago.")
    private String estadoFacturaCompra;

    // Getters y setters

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTelProveedor() {
        return telProveedor;
    }
    public void setTelProveedor(String telProveedor) {
        this.telProveedor = telProveedor;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
    }

    public String getDescripcionCompra() {
        return descripcionCompra;
    }

    public void setDescripcionCompra(String descripcionCompra) {
        this.descripcionCompra = descripcionCompra;
    }

    public String getEstadoFacturaCompra() {
        return estadoFacturaCompra;
    }

    public void setEstadoFacturaCompra(String estadoFacturaCompra) {
        this.estadoFacturaCompra = estadoFacturaCompra;
    }

}
