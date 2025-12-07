package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.rol.RolUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.venta.VentaDTO;
import com.proyecto_wyk.proyecto_wyk.entity.DetalleVenta;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.entity.Venta;
import com.proyecto_wyk.proyecto_wyk.exception.ExistenciaInsuficienteException;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import com.proyecto_wyk.proyecto_wyk.service.impl.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller // Usar @Controller para que pueda manejar VISTAS (Thymeleaf)
@RequestMapping("/ventas")
public class VentaController {
    private final VentaService ventaService;
    private final UsuarioService usuarioService; // Mantenerlo si el método GET lo usa

    public VentaController(VentaService ventaService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaVentas", ventaService.listarVenta());
        model.addAttribute("cantidadVentasExistentes", ventaService.cantidadVentasExistentes());
        return "venta/dashboardVenta";
    }

    @GetMapping("/listarDetallesVentaModal")
    @ResponseBody
    public Map<String, Object> listarDetallesVentaModal(@RequestParam("id") Long idVenta) {
        try {
            List<DetalleVenta> detalles = ventaService.findDetalleVentaByIdVenta(idVenta);

            // Usamos un HashMap para crear el mapa, garantizando el tipo Map<String, Object>
            List<Map<String, Object>> detallesDTO = detalles.stream().map(dv -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("ID_PRODUCTO", dv.getProducto().getIdProducto());
                itemMap.put("NOMBRE_PRODUCTO", dv.getProducto().getNombreProducto());
                itemMap.put("VALOR_UNITARIO_PRODUCTO", dv.getProducto().getValorUnitarioProducto());
                itemMap.put("CANTIDAD", dv.getCantidad());
                itemMap.put("SUB_TOTAL", dv.getSubTotal());

                return itemMap;
            }).collect(Collectors.toList());


            return Map.of(
                    "success", true,
                    "detalle", detallesDTO // Coincide con data.detalle en tu JS
            );

        } catch (Exception e) {
            System.err.println("Error al obtener detalle de venta: " + e.getMessage());
            return Map.of(
                    "success", false,
                    "message", "Error interno del servidor: " + e.getMessage()
            );
        }
    }

    @GetMapping("/formGuardar")
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

    @GetMapping("/formAct/{id}")
    public String mostrarFormAct(@PathVariable Long id, Model model) {
        Venta venta = ventaService.findById(id);
        model.addAttribute("formActRol", venta);
        return "venta/formActualizar";
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarRol(
            @Valid @RequestBody RolUpdateDTO dto,
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


        Rol actualRol = service.buscarPorId(dto.getIdRol());

        if (actualRol == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol no existe."
            );
        }

        if (service.existeRol(dto.getRol()) && !actualRol.getRol().equalsIgnoreCase(dto.getRol())) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un rol con ese nombre."
            );
        }

        actualRol.setRol(dto.getRol());
        actualRol.setClasificacion(clasificacionEnum);
        actualRol.setEstadoRol(dto.getEstadoRol());

        service.guardarRol(actualRol);

        return Map.of(
                "success", true,
                "message", "Rol actualizado correctamente."
        );
    }
}