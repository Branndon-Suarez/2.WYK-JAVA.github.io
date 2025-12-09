package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.dto.compra.CompraDTO;
import com.proyecto_wyk.proyecto_wyk.dto.compra.DetalleCompraModalDTO;
import com.proyecto_wyk.proyecto_wyk.dto.compra.ItemCompraDTO;
import com.proyecto_wyk.proyecto_wyk.entity.*;
import com.proyecto_wyk.proyecto_wyk.repository.CompraRepository;
import com.proyecto_wyk.proyecto_wyk.repository.DetalleCompraMateriaPrimaRepository;
import com.proyecto_wyk.proyecto_wyk.repository.DetalleCompraProductoRepository;
import com.proyecto_wyk.proyecto_wyk.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {
    private final CompraRepository compraRepository;
    private final MateriaPrimaService materiaPrimaService;
    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;

    private final DetalleCompraMateriaPrimaRepository detalleMPRepository;
    private final DetalleCompraProductoRepository detalleProdRepository;

    public CompraService(CompraRepository compraRepository, MateriaPrimaService materiaPrimaService, ProductoService productoService, UsuarioRepository usuarioRepository, DetalleCompraMateriaPrimaRepository detalleMPRepository,
                         DetalleCompraProductoRepository detalleProdRepository) {
        this.compraRepository = compraRepository;
        this.materiaPrimaService = materiaPrimaService;
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;

        this.detalleMPRepository = detalleMPRepository;
        this.detalleProdRepository = detalleProdRepository;
    }

    public List<Compra> listarCompra() {
        return compraRepository.findAll();
    }

    public long cantidadComprasExistentes() {
        return compraRepository.count();
    }

    public List<DetalleCompraModalDTO> findDetallesByIdCompra(Long idCompra) {
        List<DetalleCompraModalDTO> detallesUnificados = new ArrayList<>();

        // 1. Obtener y Mapear Detalles de Materia Prima
        List<DetalleCompraMateriaPrima> detallesMP =
                detalleMPRepository.findByCompra_IdCompra(idCompra);

        for (DetalleCompraMateriaPrima detalleMP : detallesMP) {
            Double subTotal = detalleMP.getSubTotalMatPrimaComprada().doubleValue();
            Double cantidad = detalleMP.getCantidadMatPrimaComprada().doubleValue();

            Double precioUnitario = (cantidad != 0) ? subTotal / cantidad : 0.0;

            detallesUnificados.add(new DetalleCompraModalDTO(
                    "Materia Prima",
                    detalleMP.getMateriaPrima().getNombreMateriaPrima(),
                    detalleMP.getCantidadMatPrimaComprada().intValue(),
                    precioUnitario,
                    subTotal
            ));
        }

        // 2. Obtener y Mapear Detalles de Producto
        List<DetalleCompraProducto> detallesProd =
                detalleProdRepository.findByCompra_IdCompra(idCompra);

        for (DetalleCompraProducto detalleProd : detallesProd) {
            Double subTotal = detalleProd.getSubTotalProdComprado().doubleValue();
            Double cantidad = detalleProd.getCantidadProdComprado().doubleValue();

            Double precioUnitario = (cantidad != 0) ? subTotal / cantidad : 0.0;

            detallesUnificados.add(new DetalleCompraModalDTO(
                    "Producto",
                    detalleProd.getProducto().getNombreProducto(),
                    detalleProd.getCantidadProdComprado().intValue(), // Convertir a Integer
                    precioUnitario,
                    subTotal // Ya es Double
            ));
        }

        return detallesUnificados;
    }

    @Transactional
    public Long guardarCompraCompleta(CompraDTO compraDTO, Long usuarioId) {

        // 1. Mapear DTO a Entidad Compra
        Compra compra = new Compra();

        // 1.1. Parsear la fecha del JS (ejemplo: 2025-12-08T13:00)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        try {
            compra.setFechaHoraCompra(LocalDateTime.parse(compraDTO.getFecha(), formatter));
        } catch (Exception e) {
            throw new RuntimeException("Formato de fecha/hora incorrecto: " + compraDTO.getFecha());
        }

        // 1.2. Mapear campos de proveedor y totales
        compra.setNombreProveedor(compraDTO.getNombreProveedor());
        compra.setMarca(compraDTO.getMarca());
        compra.setTelProveedor(compraDTO.getTelProveedor());
        compra.setEmailProveedor(compraDTO.getEmailProveedor());
        compra.setDescripcionCompra(compraDTO.getDescripcion());
        compra.setTotalCompra(compraDTO.getTotalCompra());

        // 1.3. Mapear Enums (Asegurando que coincida con el nombre del Enum en mayúsculas y guiones bajos)
        try {
            String tipoEnumName = compraDTO.getTipo().toUpperCase().replace(" ", "_"); // Ej: "MATERIA PRIMA" -> "MATERIA_PRIMA"
            compra.setTipo(Compra.TipoCompra.valueOf(tipoEnumName));
            compra.setEstadoFacturaCompra(Compra.EstadoFacturaCompra.valueOf(compraDTO.getEstadoCompra().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo o Estado de Compra no válidos: " + e.getMessage());
        }

        // 1.4. Mapear Usuario (ID_USUARIO_FK_COMPRA)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));
        compra.setUsuario(usuario);

        // 2. Mapear y Procesar Ítems (MP o PRODUCTO)
        List<DetalleCompraMateriaPrima> detallesMP = new ArrayList<>();
        List<DetalleCompraProducto> detallesProd = new ArrayList<>();

        for (ItemCompraDTO itemDto : compraDTO.getItems()) {
            if ("MP".equals(itemDto.getTipo())) {
                // Lógica para Materia Prima
                MateriaPrima mp = materiaPrimaService.buscarPorId(itemDto.getId());

                // *** IMPORTANTE: NO SE ACTUALIZA STOCK EN JAVA, LO HACE EL TRIGGER ***

                // Crear Detalle de Compra MP
                DetalleCompraMateriaPrima detalle = new DetalleCompraMateriaPrima();
                detalle.setCompra(compra);
                detalle.setMateriaPrima(mp);
                detalle.setCantidadMatPrimaComprada(itemDto.getCantidad());
                detalle.setSubTotalMatPrimaComprada(itemDto.getSubtotal());
                detalle.setEstadoDetCompraMatPrima(true); // Asumiendo true

                detallesMP.add(detalle);

            } else if ("PROD".equals(itemDto.getTipo())) {
                // Lógica para Producto Terminado
                Producto prod = productoService.findById(itemDto.getId());

                // *** IMPORTANTE: NO SE ACTUALIZA STOCK EN JAVA, LO HACE EL TRIGGER ***

                // Crear Detalle de Compra Producto
                DetalleCompraProducto detalle = new DetalleCompraProducto();
                detalle.setCompra(compra);
                // El detalle Producto en tu entidad original parece no tener un setter para Producto,
                // asumo que lo tienes corregido o se llama setProducto.
                detalle.setProducto(prod);
                detalle.setCantidadProdComprado(itemDto.getCantidad());
                detalle.setSubTotalProdComprado(itemDto.getSubtotal());
                detalle.setEstadoDetCompraProd(true); // Asumiendo true

                detallesProd.add(detalle);
            } else {
                throw new RuntimeException("Tipo de ítem inválido: " + itemDto.getTipo());
            }
        }

        // 3. Asignar Detalles a la Compra
        compra.setDetallesMateriaPrima(detallesMP);
        compra.setDetallesProducto(detallesProd);

        // 4. Guardar la Compra (Spring Data JPA guarda detalles en cascada)
        Compra compraGuardada = compraRepository.save(compra);

        return compraGuardada.getIdCompra();
    }

    public Compra guardarCompra(Compra venta) {
        return compraRepository.save(venta);
    }

    public Compra findById(Long id) {
        return compraRepository.findById(id).orElse(null);
    }
}
