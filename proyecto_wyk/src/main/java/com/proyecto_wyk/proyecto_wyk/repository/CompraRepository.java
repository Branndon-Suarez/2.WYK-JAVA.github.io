package com.proyecto_wyk.proyecto_wyk.repository;

import com.proyecto_wyk.proyecto_wyk.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
}