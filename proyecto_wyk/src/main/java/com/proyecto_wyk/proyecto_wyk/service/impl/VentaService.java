package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.dto.venta.DetalleVentaDTO;
import com.proyecto_wyk.proyecto_wyk.dto.venta.VentaDTO;
import com.proyecto_wyk.proyecto_wyk.entity.DetalleVenta;
import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.entity.Venta;
import com.proyecto_wyk.proyecto_wyk.exception.ExistenciaInsuficienteException;
import com.proyecto_wyk.proyecto_wyk.repository.DetalleVentaRepository;
import com.proyecto_wyk.proyecto_wyk.repository.ProductoRepository;
import com.proyecto_wyk.proyecto_wyk.repository.UsuarioRepository;
import com.proyecto_wyk.proyecto_wyk.repository.VentaRepository;
import com.proyecto_wyk.proyecto_wyk.service.IVentaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VentaService implements IVentaService {
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public VentaService(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Venta> listarVenta() {
        return ventaRepository.findAll();
    }

    @Override
    public List<DetalleVenta> findDetalleVentaByIdVenta(Long idVenta) {
        return detalleVentaRepository.findByVenta_IdVenta(idVenta);
    }

    @Override
    public long cantidadVentasExistentes() {
        return ventaRepository.count();
    }

    @Override
    public List<Venta> listarVentasFiltradas(Map<String, String> params) {

        List<Venta> lista = ventaRepository.findAll();

        // --- 1 BÚSQUEDA GLOBAL ---
        // formateador para fechaRegistro
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String search = params.get("search");
        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();

            lista.removeIf(v -> {
                // Campos de la entidad Venta
                String fechaStr = v.getFechaHoraVenta() != null ? v.getFechaHoraVenta().format(dtf).toLowerCase() : "";
                String totalStr = v.getTotalVenta() != null ? String.valueOf(v.getTotalVenta()) : "";
                String numMesaStr = v.getNumeroMesa() != null ? String.valueOf(v.getNumeroMesa()) : "";
                String descripcion = v.getDescripcion() != null ? v.getDescripcion().toLowerCase() : "";

                // Campos relacionados
                String usuarioNombre = (v.getUsuario() != null && v.getUsuario().getNombre() != null) ? v.getUsuario().getNombre().toLowerCase() : "";

                // Campos Enum/String
                String estadoPedidoStr = v.getEstadoPedido() != null ? v.getEstadoPedido().toString().toLowerCase() : "";
                String estadoPagoStr = v.getEstadoPago() != null ? v.getEstadoPago().toString().toLowerCase() : "";


                return !(
                        fechaStr.contains(s)
                                || totalStr.contains(s)
                                || numMesaStr.contains(s)
                                || descripcion.contains(s)
                                || usuarioNombre.contains(s)
                                || estadoPedidoStr.contains(s)
                                || estadoPagoStr.contains(s)
                );
            });
        }

        // --- 2 FILTROS DINÁMICOS (chips) ---
        params.forEach((clave, valor) -> {

            if (clave.startsWith("filtro_") && valor != null && !valor.isEmpty()) {

                String columna = clave.replace("filtro_", "").toUpperCase();
                // Convertir los valores de filtro a una lista para búsqueda rápida
                List<String> filtros = Arrays.stream(valor.split(","))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .collect(Collectors.toList());

                switch (columna) {
                    // Filtro por fecha (se recomienda usar solo el formato YYYY-MM-DD para el filtro)
                    case "FECHA_HORA_VENTA":
                        lista.removeIf(v -> {
                            String fechaCorta = v.getFechaHoraVenta() != null ?
                                    v.getFechaHoraVenta().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toUpperCase() : "";
                            return !filtros.contains(fechaCorta);
                        });
                        break;

                    case "TOTAL_VENTA":
                        lista.removeIf(v -> {
                            String total = v.getTotalVenta() != null ? String.valueOf(v.getTotalVenta()) : "";
                            return !filtros.contains(total);
                        });
                        break;

                    case "NUMERO_MESA":
                        lista.removeIf(v -> {
                            String mesa = v.getNumeroMesa() != null ? String.valueOf(v.getNumeroMesa()) : "";
                            return !filtros.contains(mesa);
                        });
                        break;

                    case "NOMBRE_USUARIO": // Asume que la clave de filtro es NOMBRE_USUARIO
                        lista.removeIf(v -> {
                            String usuario = (v.getUsuario() != null && v.getUsuario().getNombre() != null) ?
                                    v.getUsuario().getNombre().toUpperCase() : "";
                            return !filtros.contains(usuario);
                        });
                        break;

                    case "ESTADO_PEDIDO":
                        lista.removeIf(v -> {
                            String estadoPedido = v.getEstadoPedido() != null ?
                                    v.getEstadoPedido().toString().toUpperCase() : "";
                            return !filtros.contains(estadoPedido);
                        });
                        break;

                    case "ESTADO_PAGO":
                        lista.removeIf(v -> {
                            String estadoPago = v.getEstadoPago() != null ?
                                    v.getEstadoPago().toString().toUpperCase() : "";
                            return !filtros.contains(estadoPago);
                        });
                        break;
                }
            }
        });

        return lista;
    }

    /**
     * Guarda la Venta (Maestro) y todos sus DetalleVenta (Detalle) en una transacción atómica.
     * Si el stock es insuficiente, lanza una excepción y revierte toda la operación.
     * @param ventaDTO Los datos del pedido desde el frontend.
     * @return La entidad Venta guardada.
     */
    @Transactional
    public Venta guardarVentaCompleta(VentaDTO ventaDTO) {
        // 1. Mapeo y dependencias
        LocalDateTime fechaVenta = LocalDateTime.parse(ventaDTO.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Buscar al Usuario (empleado que realiza la venta)
        Usuario usuario = usuarioRepository.findById(ventaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + ventaDTO.getUsuarioId()));

        // 2. Crear y Guardar la Venta (Maestro)
        Venta venta = new Venta();
        venta.setFechaHoraVenta(fechaVenta);
        venta.setNumeroMesa(ventaDTO.getMesa());
        venta.setDescripcion(ventaDTO.getDescripcion());
        venta.setEstadoPedido(ventaDTO.getEstadoPedido());
        venta.setEstadoPago(ventaDTO.getEstadoPago());
        venta.setTotalVenta(ventaDTO.getTotal());
        venta.setUsuario(usuario);

        Venta ventaGuardada = ventaRepository.save(venta);

        // 3. Iterar, Validar Stock y Guardar los Detalles
        for (DetalleVentaDTO detalleDTO : ventaDTO.getProductos()) {

            // a) Buscar Producto
            Producto producto = productoRepository.findById(detalleDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getId()));

            // b) Validar Stock (la cantidad seleccionada no debe ser mayor a la existencia)
            if (producto.getCantExistProducto() < detalleDTO.getCantidad()) {
                throw new ExistenciaInsuficienteException("Stock insuficiente para: " + producto.getNombreProducto() +
                        ". Stock actual: " + producto.getCantExistProducto());
            }

            // c) Crear y Guardar Detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(ventaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());

            // El subtotal es la multiplicación de (Cantidad * Precio Unitario)
            Double subTotal = detalle.getCantidad() * detalleDTO.getPrecio();
            detalle.setSubTotal(subTotal);

            detalleVentaRepository.save(detalle);

            // NOTA: Gracias al trigger de la BD 'TR_RESTAR_STOCK_DESPUES_VENTA' el stock de la cantidad de existencia de producto se actualizará
        }

        return ventaGuardada;
    }

    @Override
    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }
}
