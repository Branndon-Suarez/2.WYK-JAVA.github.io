package com.proyecto_wyk.proyecto_wyk.service.impl;

import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import com.proyecto_wyk.proyecto_wyk.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodosActivos() {
        // Usamos el método de Query Method del repositorio
        return productoRepository.findByEstadoProductoTrue();
    }
}
