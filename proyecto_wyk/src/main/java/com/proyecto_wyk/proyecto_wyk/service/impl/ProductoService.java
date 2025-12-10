package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProducto() {
        return productoRepository.findAll();
    }

    public List<Producto> listarTodosActivos() {
        // Usamos el método de Query Method del repositorio
        return productoRepository.findByEstadoProductoTrue();
    }

    public long cantProdExistentes() {
        return productoRepository.count();
    }

    public long cantProdActivos() {
        return productoRepository.findAll().stream()
                .filter(Producto::getEstadoProducto)
                .count();
    }

    public long cantProdInactivos() {
        return productoRepository.findAll().stream()
                .filter(u -> !u.getEstadoProducto())
                .count();
    }

    public Producto guardarProd(Producto producto) {
        return productoRepository.save(producto);
    }

    // Lógica de existencia unificada
    public boolean existeOtroProductoConMismoNombre(String nombreProducto, Long idProductoActual) {
        if (idProductoActual == null) {
            // Modo Creación (ID desconocido/nuevo): Verifica si el nombre existe en CUALQUIER producto.
            return productoRepository.existsByNombreProducto(nombreProducto);
        } else {
            // Modo Actualización: Verifica si el nombre existe en OTRO producto (excluyendo el ID actual).
            return productoRepository.existsByNombreProductoAndIdProductoNot(nombreProducto, idProductoActual);
        }
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public void eliminarProd(Long id) {
        productoRepository.deleteById(id);
    }
}
