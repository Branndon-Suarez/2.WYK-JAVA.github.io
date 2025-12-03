package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.Tarea.TareaCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.Tarea.TareaUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Tarea;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.service.impl.TareaService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import jakarta.validation.Valid;
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

        return "tareas/dashboardTarea";
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
        if (result.hasErrors()) {
            return Map.of(
                    "success", false,
                    "message", result.getFieldError().getDefaultMessage()
            );
        }

        if (tareaService.existeTarea(Long.valueOf(dto.getTarea()))) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe esa tarea dentro del sistema."
            );
        }
        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setTarea(dto.getTarea());
        nuevaTarea.setCategoria(dto.getCategoria());
        nuevaTarea.setDescripcion(dto.getDescripcion());
        nuevaTarea.setTiempoEstimadoHoras(Float.valueOf(dto.getTiempoEstimadoHoras()));
        nuevaTarea.setPrioridad(dto.getPrioridad());
        nuevaTarea.setEstadoTarea(Tarea.EstadoTarea.PENDIENTE);

        // --- ASIGNAR USUARIO CORRECTAMENTE ---
        Usuario usuario = usuarioService.buscarPorID(Long.valueOf(dto.getIdUsuarioAsignado()));
        if (usuario == null) {
            return Map.of(
                    "success", false,
                    "message", "La tarea seleccionada no existe.");
        }
        nuevaTarea.setUsuarioAsignado(usuario);

        tareaService.guardarTarea(nuevaTarea);

        return Map.of(
                "success", true,
                "message", "Tarea registrada correctamente."
        );
    }
    //Falta el usuario creador
}
