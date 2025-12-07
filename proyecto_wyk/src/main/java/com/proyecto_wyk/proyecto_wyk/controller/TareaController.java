package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.Tarea.TareaCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.Tarea.TareaUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
import com.proyecto_wyk.proyecto_wyk.service.impl.TareaService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/tareas")
public class TareaController {
    private final TareaService tareaService;
    private final UsuarioService usuarioService;

    public TareaController(TareaService tareaService, UsuarioService usuarioService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaTareas", tareaService.listarTarea());
        model.addAttribute("tareasExistentes", tareaService.cantTareasExistentes());
        model.addAttribute("tareasPendientes", tareaService.cantTareasPendientes());
        model.addAttribute("tareasCompletadas", tareaService.cantTareasCompletada());
        model.addAttribute("tareasCanceladas", tareaService.cantTareasCancelada());

        return "tarea/dashboardTarea";
    }

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarTarea", new TareaCreateDTO());
        model.addAttribute("listaUsuraio", usuarioService.listarUsuario());

        return "tarea/formGuardar";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarTarea(
            @Valid @RequestBody TareaCreateDTO dto,
            BindingResult result
    ) {
        // --- 1. OBTENER EL USUARIO CREADOR LOGUEADO ---

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioCreador = null;

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            usuarioCreador = userDetails.getUsuario();
        }

        if (usuarioCreador == null) {
            // Esto significa que el usuario no está correctamente autenticado.
            return Map.of(
                    "success", false,
                    "message", "Error de autenticación. El usuario creador no pudo ser identificado."
            );
        }

        if (result.hasErrors()) {
            String mensaje = result.getFieldErrors().stream()
                    .filter(e -> e.getCode().equals("NotBlank")) // 1. Busca si existe un error de @NotBlank para darle prioridad
                    .findFirst()
                    .orElse(result.getFieldError())// 2. Si no es @NotBlank, toma el primer error que encuentre
                    .getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        if (tareaService.existeTarea(dto.getTarea())) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe esa tarea dentro del sistema."
            );
        }

        // Conversión de String a Enum y try catch para manejar atacante e impedir no enviar datos que no estén en el enum.
        Tarea.Prioridad prioridadEnum;
        try {
            prioridadEnum = Tarea.Prioridad.valueOf(dto.getPrioridad().toUpperCase()); // Asegurar mayúsculas
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", "Prioridad no válida. Por favor, selecciona un valor de la lista."
            );
        }

        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setTarea(dto.getTarea());
        nuevaTarea.setCategoria(dto.getCategoria());
        nuevaTarea.setDescripcion(dto.getDescripcion());
        nuevaTarea.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
        nuevaTarea.setPrioridad(prioridadEnum);
        nuevaTarea.setEstadoTarea(Tarea.EstadoTarea.PENDIENTE);

        // --- ASIGNAR USUARIO ASIGNADO ---
        Usuario usuarioAsignado = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuarioAsignado()));
        if (usuarioAsignado == null) {
            return Map.of(
                    "success", false,
                    "message", "El usuario asignado no existe.");
        }
        nuevaTarea.setUsuarioAsignado(usuarioAsignado);

        // --- ASIGNAR USUARIO CREADOR (FK: Usuario Logueado) ---
        nuevaTarea.setUsuarioCreador(usuarioCreador);

        tareaService.guardarTarea(nuevaTarea);

        return Map.of(
                "success", true,
                "message", "Tarea registrada correctamente."
        );
    }
    @GetMapping("/formAct/{id}")
    public String mostrarFormActualizar(@PathVariable Long id, Model model) {

        Tarea tarea = tareaService.buscarPorID(id);

        model.addAttribute("formActTarea", tarea);
        model.addAttribute("listaUsurarios", usuarioService.listarUsuario());

        return "tarea/formActualizar";
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarTarea(
            @Valid @RequestBody TareaUpdateDTO dto,
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

        Tarea actualTarea = tareaService.buscarPorID(dto.getIdTarea());

        if (actualTarea == null) {
            return Map.of(
                    "success", false,
                    "message", "El usuario no existe."
            );
        }

        // validar documento duplicado
        if (tareaService.existeTarea(dto.getTarea())
                && !actualTarea.getTarea().equals(dto.getTarea())) {

            return Map.of(
                    "success", false,
                    "message", "Ya existe un usuario con ese número de documento."
            );
        }

        actualTarea.setIdTarea(Long.valueOf(dto.getIdTarea()));
        actualTarea.setTarea(dto.getTarea());
        actualTarea.setCategoria(dto.getCategoria());
        actualTarea.setDescripcion(dto.getDescripcion());
        actualTarea.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
        actualTarea.setPrioridad(dto.getPrioridad());
        actualTarea.setEstadoTarea(dto.getEstadoTarea());

        // --- ASIGNAR USUARIO CORRECTAMENTE ---
        Usuario usuario = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuarioAsignado()));
        if (usuario == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol seleccionado no existe.");
        }
        actualTarea.setNombre(usuario);

        tareaService.guardarTarea(actualTarea);

        return Map.of(
                "success", true,
                "message", "Usuario actualizado correctamente."
        );
    }
}
