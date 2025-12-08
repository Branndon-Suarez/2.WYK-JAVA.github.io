package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.DetalleCompraProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCompraProductoRepository extends JpaRepository<DetalleCompraProducto, Integer> {
}
