package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.dto.produccion.DetalleProduccionDTO;
import com.proyecto_wyk.proyecto_wyk.dto.produccion.ProduccionDTO;
import com.proyecto_wyk.proyecto_wyk.entity.*;
import com.proyecto_wyk.proyecto_wyk.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduccionService {
    private final ProduccionRepository produccionRepository;
    private final DetalleProduccionRepository detalleProduccionRepository;
    private final MateriaPrimaRepository materiaPrimaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProduccionService(ProduccionRepository produccionRepository, DetalleProduccionRepository detalleRepository,
                             MateriaPrimaRepository mpRepository, ProductoRepository productoRepository,
                             UsuarioRepository usuarioRepository) {
        this.produccionRepository = produccionRepository;
        this.detalleProduccionRepository = detalleRepository;
        this.materiaPrimaRepository = mpRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Produccion> listarProduccion() {
        return produccionRepository.findAll();
    }

    public List<DetalleProduccion> findDetalleProduccionByIdProduccion(Long idProduccion) {
        return detalleProduccionRepository.findByProduccion_IdProduccion(idProduccion);
    }

    public long cantidadProduccionesExistentes() {
        return produccionRepository.count();
    }

    @Transactional
    public Produccion guardarProduccionCompleta(ProduccionDTO dto) {
        // 1. Obtener Entidades Base
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 2. Guardar Maestro
        Produccion p = new Produccion();
        p.setNombreProduccion(dto.getNombre());
        p.setCantProduccion(dto.getCantidadAProducir());
        p.setDescripcionProduccion(dto.getDescripcion());
        p.setEstadoProduccion(Produccion.EstadoProduccion.FINALIZADA);
        p.setProducto(producto);
        p.setUsuario(usuario);
        Produccion produccionGuardada = produccionRepository.save(p);

        // 3. Procesar Detalles e Inventario
        for (DetalleProduccionDTO detalleProduccionDTO : dto.getInsumos()) {
            MateriaPrima mp = materiaPrimaRepository.findById(detalleProduccionDTO.getMateriaPrimaId())
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

            if (mp.getCantidadExistMateriaPrima() < detalleProduccionDTO.getCantidadRequerida()) {
                throw new RuntimeException("Stock insuficiente de: " + mp.getNombreMateriaPrima());
            }

            // Crear Detalle
            DetalleProduccion det = new DetalleProduccion();
            det.setProduccion(produccionGuardada);
            det.setMateriaPrima(mp);
            det.setCantidadRequerida(detalleProduccionDTO.getCantidadRequerida());
            detalleProduccionRepository.save(det);
        }

        // 4. CAMBIAR ESTADO A FINALIZADA (UPDATE)
        // Esto activará automáticamente el Trigger: TR_SUMAR_PROD_PRODUC_ESTADO_FINISH
        produccionGuardada.setEstadoProduccion(Produccion.EstadoProduccion.FINALIZADA);
        produccionRepository.save(produccionGuardada);

        return produccionGuardada;
    }
}
