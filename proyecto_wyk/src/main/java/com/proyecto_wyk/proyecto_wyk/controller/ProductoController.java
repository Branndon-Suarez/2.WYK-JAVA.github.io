package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.producto.ProductoCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.producto.ProductoUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Producto;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.ProductoService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaProd", productoService.listarProducto());
        model.addAttribute("prodExistentes", productoService.cantProdExistentes());
        model.addAttribute("prodActivos", productoService.cantProdActivos());
        model.addAttribute("prodInactivos", productoService.cantProdInactivos());

        return "producto/dashboardProducto";
    }

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<?> listarProductosModal() {
        try {
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

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarProd", new UsuarioCreateDTO());
        return "producto/formGuardar";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarProducto(
            @Valid @RequestBody ProductoCreateDTO dto,
            BindingResult result
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioCreador = null;

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            usuarioCreador = userDetails.getUsuario();
        }

        if (usuarioCreador == null) {
            return Map.of(
                    "success", false,
                    "message", "Error de autenticación. El usuario creador no pudo ser identificado."
            );
        }

        if (result.hasErrors()) {
            String mensaje = result.getFieldErrors().stream()
                    .filter(e -> e.getCode().equals("NotBlank"))
                    .findFirst()
                    .orElse(result.getFieldError())
                    .getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        // Validar si el ID ya existe
        if (productoService.findById(dto.getIdProducto()) != null) {
            return Map.of(
                    "success", false,
                    "message", "El ID de producto " + dto.getIdProducto() + " ya está en uso."
            );
        }

        // Validar si el Nombre ya existe
        if (productoService.existeOtroProductoConMismoNombre(dto.getNombreProducto(), null)) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un producto con ese nombre."
            );
        }

        Producto nuevoProd = new Producto();
        nuevoProd.setIdProducto(dto.getIdProducto());
        nuevoProd.setNombreProducto(dto.getNombreProducto());
        nuevoProd.setValorUnitarioProducto(Long.valueOf(dto.getValorUnitarioProducto()));
        nuevoProd.setCantExistProducto(dto.getCantExistProducto());
        nuevoProd.setFechaVencimientoProducto(dto.getFechaVencimientoProducto());
        nuevoProd.setTipoProducto(dto.getTipoProducto());

        // --- ASIGNAR USUARIO CREADOR (FK: Usuario Logueado) ---
        nuevoProd.setUsuario(usuarioCreador);

        nuevoProd.setEstadoProducto(true);

        productoService.guardarProd(nuevoProd);

        return Map.of(
                "success", true,
                "message", "Producto registrado correctamente."
        );
    }

    @GetMapping("/formAct/{id}")
    public String mostrarFormActualizar(@PathVariable Long id, Model model) {

        Producto producto = productoService.findById(id);

        model.addAttribute("formActProd", producto);

        return "producto/formActualizar";
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarProducto(
            @Valid @RequestBody ProductoUpdateDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            String mensaje = result.getFieldErrors().stream()
                    .filter(e -> e.getCode().equals("NotBlank"))
                    .findFirst()
                    .orElse(result.getFieldError())
                    .getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        Producto actualProd = productoService.findById(dto.getIdProducto());

        if (actualProd == null) {
            return Map.of(
                    "success", false,
                    "message", "El producto no existe."
            );
        }

        // Validar que el nuevo nombre no exista en otro producto (excluyendo el ID actual)
        if (productoService.existeOtroProductoConMismoNombre(dto.getNombreProducto(), dto.getIdProducto())) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe otro producto con ese nombre."
            );
        }

        actualProd.setNombreProducto(dto.getNombreProducto());
        actualProd.setValorUnitarioProducto(Long.valueOf(dto.getValorUnitarioProducto()));
        actualProd.setCantExistProducto(dto.getCantExistProducto());
        actualProd.setFechaVencimientoProducto(dto.getFechaVencimientoProducto());
        actualProd.setTipoProducto(dto.getTipoProducto());
        actualProd.setEstadoProducto(dto.isEstadoProducto());

        productoService.guardarProd(actualProd);

        return Map.of(
                "success", true,
                "message", "Producto actualizado correctamente."
        );
    }

    @PostMapping("/updateState")
    @ResponseBody
    public Map<String, Object> updateState(@RequestBody Map<String, Object> body) {
        try {
            Long id = Long.valueOf(body.get("id").toString());
            Integer estado = Integer.parseInt(body.get("estado").toString());

            boolean nuevoEstado = estado == 1;

            Producto producto = productoService.findById(id);

            if (producto == null) {
                return Map.of(
                        "success", false,
                        "message", "El producto no existe."
                );
            }

            producto.setEstadoProducto(nuevoEstado);
            productoService.guardarProd(producto);

            return Map.of(
                    "success", true,
                    "message", "Estado actualizado correctamente.",
                    "estadoNuevo", nuevoEstado
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al actualizar estado: " + e.getMessage()
            );
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> eliminarUsuario(@RequestParam Long id) {
        try {
            productoService.eliminarProd(id);

            return Map.of(
                    "success", true,
                    "message", "Usuario eliminado correctamente."
            );

        } catch (DataIntegrityViolationException e) {
            return Map.of(
                    "success", false,
                    "code", "FK_CONSTRAINT",
                    "message", "No es posible eliminar este usuario porque está relacionado con otros registros."
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al eliminar usuario: " + e.getMessage()
            );
        }
    }
}
