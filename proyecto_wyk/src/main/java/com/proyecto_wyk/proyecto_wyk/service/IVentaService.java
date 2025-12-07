package com.proyecto_wyk.proyecto_wyk.service;

import com.proyecto_wyk.proyecto_wyk.dto.venta.VentaDTO;
import com.proyecto_wyk.proyecto_wyk.entity.DetalleVenta;
import com.proyecto_wyk.proyecto_wyk.entity.Venta;

import java.util.List;
import java.util.Map;

public interface IVentaService {
    List<Venta> listarVenta();
    List<DetalleVenta> findDetalleVentaByIdVenta(Long idVenta);
    long cantidadVentasExistentes();
    List<Venta> listarVentasFiltradas(Map<String, String> params);

    Venta guardarVentaCompleta(VentaDTO ventaDTO);

    Venta findById(Long id);
}
