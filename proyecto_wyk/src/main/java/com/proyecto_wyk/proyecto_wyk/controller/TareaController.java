package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.service.impl.TareaService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "tareas/dashboardTarea";
    }

}
