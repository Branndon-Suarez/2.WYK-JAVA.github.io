package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.DetalleCompraProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraProductoRepository extends JpaRepository<DetalleCompraProducto, Integer> {
    List<DetalleCompraProducto> findByCompra_IdCompra(Long idCompra);
}
