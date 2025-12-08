package com.proyecto_wyk.proyecto_wyk.dto.compra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class CompraDTO {
    @NotBlank(message = "La fecha es obligatoria")
    private String fecha; // Formato: yyyy-MM-dd'T'HH:mm

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombreProveedor;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotNull(message = "El teléfono del proveedor es obligatorio")
    private Long telProveedor;

    @NotBlank(message = "El email del proveedor es obligatorio")
    private String emailProveedor;

    @NotBlank(message = "El tipo de compra es obligatorio")
    private String tipo; // MATERIA PRIMA o PRODUCTO TERMINADO

    @NotBlank(message = "El estado de la compra es obligatorio")
    private String estadoCompra; // PENDIENTE, PAGADA, CANCELADA

    private String descripcion; // Opcional

    @Positive(message = "El total de la compra debe ser mayor a cero")
    private Long totalCompra;

    // Detalle de los ítems
    @NotNull(message = "La lista de ítems no puede ser nula")
    private List<ItemCompraDTO> items;


    // --- Getters y Setters (Necesarios para el mapeo JSON) ---

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Long getTelProveedor() { return telProveedor; }
    public void setTelProveedor(Long telProveedor) { this.telProveedor = telProveedor; }

    public String getEmailProveedor() { return emailProveedor; }
    public void setEmailProveedor(String emailProveedor) { this.emailProveedor = emailProveedor; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstadoCompra() { return estadoCompra; }
    public void setEstadoCompra(String estadoCompra) { this.estadoCompra = estadoCompra; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getTotalCompra() { return totalCompra; }
    public void setTotalCompra(Long totalCompra) { this.totalCompra = totalCompra; }

    public List<ItemCompraDTO> getItems() { return items; }
    public void setItems(List<ItemCompraDTO> items) { this.items = items; }
}
