package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.produccion.ProduccionDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Produccion;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.ProduccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/produccion")
public class ProduccionController {

    private final ProduccionService produccionService;

    public ProduccionController(ProduccionService produccionService) {
        this.produccionService = produccionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaProduccion", produccionService.listarProduccion());
        model.addAttribute("cantidadProduccionesExistentes", produccionService.cantidadProduccionesExistentes());
        return "produccion/dashboardProduccion";
    }

    @GetMapping("/formGuardar")
    public String mostrarCrearProduccion(Model model) {
        // Lógica de seguridad idéntica a la de Ventas/Compras
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            model.addAttribute("currentUserId", user.getUsuario().getIdUsuario());
            model.addAttribute("rolClasificacion", user.getUsuario().getRol().getClasificacion());
        }
        return "produccion/produccionCompleta";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarProduccion(@RequestBody ProduccionDTO dto) {
        try {
            Produccion guardada = produccionService.guardarProduccionCompleta(dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Producción registrada exitosamente",
                    "idProduccion", guardada.getIdProduccion()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
}
