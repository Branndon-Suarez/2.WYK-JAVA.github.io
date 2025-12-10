package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEstadoProductoTrue();

    boolean existsByNombreProducto(String nombreProducto);
    // Verificar si existe otro producto con el mismo nombre y que NO sea el ID actual
    boolean existsByNombreProductoAndIdProductoNot(String nombreProducto, Long idProducto);
}
