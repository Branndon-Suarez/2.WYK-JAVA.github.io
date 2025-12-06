package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import com.proyecto_wyk.proyecto_wyk.service.impl.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Endpoint utilizado por pedidosMesero.js para llenar el modal de productos
    @GetMapping("/listar")
    public ResponseEntity<?> listarProductosParaModal() {
        try {
            // Este método debe devolver todos los productos necesarios para la venta.
            List<Producto> productos = productoService.listarTodosActivos();

            // Spring Boot convierte automáticamente la lista de entidades a JSON.
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            System.err.println("Error al listar productos para modal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al cargar el listado de productos."
            ));
        }
    }
}
