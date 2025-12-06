package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.venta.VentaDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.entity.Venta;
import com.proyecto_wyk.proyecto_wyk.exception.ExistenciaInsuficienteException;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import com.proyecto_wyk.proyecto_wyk.service.impl.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller // Usar @Controller para que pueda manejar VISTAS (Thymeleaf)
@RequestMapping("/ventas")
public class VentaController {
    private final VentaService ventaService;
    private final UsuarioService usuarioService; // Mantenerlo si el método GET lo usa

    public VentaController(VentaService ventaService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/crear")
    public String mostrarCrearVenta(Model model) {

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
        return "venta/ventaCompleta";
    }

    // --- 2. ENDPOINT PARA GUARDAR VENTA (POST AJAX) ---
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarVenta(@RequestBody VentaDTO ventaDTO) {
        try {
            Venta ventaGuardada = ventaService.guardarVentaCompleta(ventaDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pedido guardado con éxito.",
                    "idVenta", ventaGuardada.getIdVenta()
            ));
        } catch (ExistenciaInsuficienteException e) {
            // Error de negocio: Stock insuficiente
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error de inventario: " + e.getMessage()
            ));
        } catch (Exception e) {
            // Error interno
            System.err.println("Error al guardar venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor al procesar el pedido."
            ));
        }
    }
}