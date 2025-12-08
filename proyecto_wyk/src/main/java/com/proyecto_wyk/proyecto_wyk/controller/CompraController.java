package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.compra.CompraDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.CompraService;
import com.proyecto_wyk.proyecto_wyk.service.impl.MateriaPrimaService;
import com.proyecto_wyk.proyecto_wyk.service.impl.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/compras")
public class CompraController {
    private final CompraService compraService;
    private final MateriaPrimaService materiaPrimaService;
    private final ProductoService productoService;

    public CompraController(CompraService compraService, MateriaPrimaService materiaPrimaService, ProductoService productoService) {
        this.compraService = compraService;
        this.materiaPrimaService = materiaPrimaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaVentas", compraService.listarCompra());
        model.addAttribute("cantidadComprasExistentes", compraService.cantidadComprasExistentes());
        return "venta/dashboardVenta";
    }

    // --- ENDPOINT AJAX para Materia Prima (Referenciado por el JS como 'compras/listarMateriaPrimaAjax') ---
    @GetMapping("/listarMateriaPrimaAjax")
    @ResponseBody
    public List<Map<String, Object>> listarMateriaPrimaAjax() {
        try {
            List<Map<String, Object>> itemsDTO = materiaPrimaService.listarTodas().stream().map(mp -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("ID_MATERIA_PRIMA", mp.getIdMateriaPrima());
                itemMap.put("NOMBRE_MATERIA_PRIMA", mp.getNombreMateriaPrima());
                // Aseguramos que se envía como entero o Long (compatibles con Long)
                itemMap.put("VALOR_UNITARIO_MAT_PRIMA", mp.getValorUnitarioMatPrima());
                itemMap.put("CANTIDAD_EXIST_MATERIA_PRIMA", mp.getCantidadExistMateriaPrima());
                itemMap.put("PRESENTACION_MATERIA_PRIMA", mp.getPresentacionMateriaPrima());
                return itemMap;
            }).collect(Collectors.toList());

            return itemsDTO;

        } catch (Exception e) {
            System.err.println("Error al obtener lista de Materia Prima para compra: " + e.getMessage());
            return List.of();
        }
    }

    // --- ENDPOINT AJAX para Producto Terminado (Referenciado por el JS como 'compras/listarProductosAjax') ---
    @GetMapping("/listarProductosAjax")
    @ResponseBody
    public List<Map<String, Object>> listarProductosAjax() {
        try {
            List<Map<String, Object>> itemsDTO = productoService.listarTodosActivos().stream().map(prod -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("ID_PRODUCTO", prod.getIdProducto());
                itemMap.put("NOMBRE_PRODUCTO", prod.getNombreProducto());
                itemMap.put("VALOR_UNITARIO_PRODUCTO", prod.getValorUnitarioProducto());
                itemMap.put("CANT_EXIST_PRODUCTO", prod.getCantExistProducto());
                return itemMap;
            }).collect(Collectors.toList());

            return itemsDTO;

        } catch (Exception e) {
            System.err.println("Error al obtener lista de Productos para compra: " + e.getMessage());
            return List.of();
        }
    }

    @GetMapping("/formGuardar")
    public String mostrarCrearCompra(Model model) {

        // 1. Obtener el contexto de seguridad actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioLogueado = null;

        // 2. Verificar si la autenticación es válida y obtener la entidad Usuario
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            usuarioLogueado = userDetails.getUsuario();
        }

        // 3. Manejo de error si el usuario no está autenticado correctamente
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // 4. Extraer el ID y la Clasificación del Rol
        Long userId = usuarioLogueado.getIdUsuario();
        String rolClasificacion = usuarioLogueado.getRol().getClasificacion().toString();

        // 5. Pasar las variables al modelo para que Thymeleaf las use
        model.addAttribute("currentUserId", userId);
        model.addAttribute("rolClasificacion", rolClasificacion);

        // 6. Retornar la plantilla
        return "compra/compraCompleta";
    }

    // --- ENDPOINT PARA GUARDAR COMPRA (POST AJAX: 'compras/create') ---
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarCompra(
            @Valid @RequestBody CompraDTO compraDTO)
    {
        // ... (Lógica para obtener usuarioId y llamar a compraService.guardarCompraCompleta) ...
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Usuario no autenticado para realizar la compra."
            ));
        }
        Long usuarioId = ((CustomUserDetails) authentication.getPrincipal()).getUsuario().getIdUsuario();

        try {
            Long idCompraGuardada = compraService.guardarCompraCompleta(compraDTO, usuarioId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Compra guardada con éxito.",
                    "idCompra", idCompraGuardada
            ));
        } catch (RuntimeException e) { // Usamos RuntimeException para simplificar, puedes usar tu ValidationException
            System.err.println("Error de lógica de negocio al guardar compra: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al procesar la compra: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Error al guardar compra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor al procesar la compra."
            ));
        }
    }
}
