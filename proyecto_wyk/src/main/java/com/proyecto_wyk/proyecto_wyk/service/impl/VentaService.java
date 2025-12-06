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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class VentaService {
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

    /**
     * Guarda la Venta (Maestro) y todos sus DetalleVenta (Detalle) en una transacción atómica.
     * Si el stock es insuficiente, lanza una excepción y revierte toda la operación.
     * @param ventaDTO Los datos del pedido desde el frontend.
     * @return La entidad Venta guardada.
     */
    @Transactional
    public Venta guardarVentaCompleta(VentaDTO ventaDTO) {

        // 1. Mapeo y dependencias

        // El JS envía 'YYYY-MM-DDTmm:ss', usamos el formatter estándar de Java.
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
            // Ambos son Long/Integer
            if (producto.getCantExistProducto() < detalleDTO.getCantidad()) {
                throw new ExistenciaInsuficienteException("Stock insuficiente para: " + producto.getNombreProducto() +
                        ". Stock actual: " + producto.getCantExistProducto());
            }

            // c) Crear y Guardar Detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(ventaGuardada);
            detalle.setProducto(producto);
            // La cantidad en la BD es BIGINT, mapeada a Long.
            detalle.setCantidad(detalleDTO.getCantidad());

            // El subtotal es la multiplicación de (Cantidad * Precio Unitario)
            // Multiplicamos como Long y lo redondeamos (ya que el total en BD es BIGINT)
            Double subTotal = detalle.getCantidad() * detalleDTO.getPrecio();
            detalle.setSubTotal(subTotal);

            detalleVentaRepository.save(detalle);

            /** * NOTA: Es fundamental que tengas un TRIGGER en tu base de datos (MySQL)
             * en la tabla DETALLE_VENTA que se ejecute DESPUÉS de una inserción (AFTER INSERT)
             * para restar la cantidad vendida al campo CANT_EXIST_PRODUCTO en la tabla PRODUCTO.
             * Sin el trigger, el stock no se actualizará.
             */
        }

        return ventaGuardada;
    }
}
