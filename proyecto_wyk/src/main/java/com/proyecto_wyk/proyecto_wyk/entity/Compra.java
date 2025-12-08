package com.proyecto_wyk.proyecto_wyk.entity;

import com.proyecto_wyk.proyecto_wyk.config.TipoCompraConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "COMPRA")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPRA")
    private Long idCompra;

    @Column(name = "FECHA_HORA_COMPRA", nullable = false)
    private LocalDateTime fechaHoraCompra;

    @Convert(converter = TipoCompraConverter.class)
    @Column(name = "TIPO")
    private TipoCompra tipo;

    @Column(name = "TOTAL_COMPRA", nullable = false)
    private Long totalCompra;

    // Campos de Proveedor
    @Column(name = "NOMBRE_PROVEEDOR", nullable = false, length = 50)
    private String nombreProveedor;
    @Column(name = "MARCA", nullable = false, length = 50)
    private String marca;
    @Column(name = "TEL_PROVEEDOR", unique = true, nullable = false)
    private Long telProveedor;
    @Column(name = "EMAIL_PROVEEDOR", unique = true, nullable = false, length = 50)
    private String emailProveedor;

    @Column(name = "DESCRIPCION_COMPRA", length = 200)
    private String descripcionCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_FACTURA_COMPRA", nullable = false)
    private EstadoFacturaCompra estadoFacturaCompra; // 'PENDIENTE', 'PAGADA', 'CANCELADA'

    // Relaciones de Detalle (Cascada para guardar los detalles al guardar la Compra)
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompraMateriaPrima> detallesMateriaPrima;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompraProducto> detallesProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_FK_COMPRA", referencedColumnName = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    public Compra() {}

    // --- Enums ---
    public enum TipoCompra { MATERIA_PRIMA, PRODUCTO_TERMINADO }
    public enum EstadoFacturaCompra { PENDIENTE, PAGADA, CANCELADA }

    // --- Getters y Setters ---

    public Long getIdCompra() { return idCompra; }
    public void setIdCompra(Long idCompra) { this.idCompra = idCompra; }

    public LocalDateTime getFechaHoraCompra() { return fechaHoraCompra; }
    public void setFechaHoraCompra(LocalDateTime fechaHoraCompra) { this.fechaHoraCompra = fechaHoraCompra; }

    public TipoCompra getTipo() { return tipo; }
    public void setTipo(TipoCompra tipo) { this.tipo = tipo; }

    public Long getTotalCompra() { return totalCompra; }
    public void setTotalCompra(Long totalCompra) { this.totalCompra = totalCompra; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Long getTelProveedor() { return telProveedor; }
    public void setTelProveedor(Long telProveedor) { this.telProveedor = telProveedor; }

    public String getEmailProveedor() { return emailProveedor; }
    public void setEmailProveedor(String emailProveedor) { this.emailProveedor = emailProveedor; }

    public String getDescripcionCompra() { return descripcionCompra; }
    public void setDescripcionCompra(String descripcionCompra) { this.descripcionCompra = descripcionCompra; }

    public EstadoFacturaCompra getEstadoFacturaCompra() { return estadoFacturaCompra; }
    public void setEstadoFacturaCompra(EstadoFacturaCompra estadoFacturaCompra) { this.estadoFacturaCompra = estadoFacturaCompra; }

    public List<DetalleCompraMateriaPrima> getDetallesMateriaPrima() { return detallesMateriaPrima; }
    public void setDetallesMateriaPrima(List<DetalleCompraMateriaPrima> detallesMateriaPrima) { this.detallesMateriaPrima = detallesMateriaPrima; }

    public List<DetalleCompraProducto> getDetallesProducto() { return detallesProducto; }
    public void setDetallesProducto(List<DetalleCompraProducto> detallesProducto) { this.detallesProducto = detallesProducto; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
