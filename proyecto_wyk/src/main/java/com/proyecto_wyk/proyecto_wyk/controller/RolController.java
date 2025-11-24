package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import com.proyecto_wyk.proyecto_wyk.service.impl.RolService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/roles")
public class RolController {
    public final RolService service;

    public RolController(RolService service) {
        this.service = service;
    }

    @GetMapping("/home")
    public String listar(Model model) {
        model.addAttribute("listaRoles", service.listarRol());
        model.addAttribute("rolesExistentes", service.cantRolesExistentes());
        model.addAttribute("rolesActivos", service.cantRolesActivos());
        model.addAttribute("rolesInactivos", service.cantRolesInactivos());
        return "rol/dashboardRol";
    }

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarRol", new Rol());
        return "rol/formGuardar";
    }

    /* Forma tradicional
    @PostMapping("/guardar")
    public String guardarRol(@ModelAttribute Rol rol) {
        service.guardarRol(rol);
        return "redirect:/roles/home";
    }*/

    // Forma para devolver JSON y hacer AJAX
    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarRol(
            @RequestParam String rol,
            @RequestParam Rol.Clasificacion clasificacion
    ) {
        Rol nuevo = new Rol();
        nuevo.setRol(rol);
        nuevo.setClasificacion(clasificacion);
        nuevo.setEstadoRol(true);

        if (service.existeRol(rol)) {
            return Map.of("success", false, "message", "El rol ya existe.");
        }

        service.guardarRol(nuevo);

        return Map.of("success", true, "message", "Rol creado correctamente.");
    }

    @GetMapping("/formAct/{id}")
    public String mostrarFormAct(@PathVariable Integer id, Model model) {
        Rol rol = service.buscarPorId(id);
        model.addAttribute("formActRol", rol);
        return "rol/formActualizar";
    }

    /*Forma tradicional
    @PostMapping("/actualizar")
    public String actualizarRol(@ModelAttribute Rol rol) {
        service.guardarRol(rol);
        return "redirect:/roles/home";
    }*/
    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarRol(
            @RequestParam Integer idRol,
            @RequestParam String rol,
            @RequestParam Rol.Clasificacion clasificacion,
            @RequestParam boolean estadoRol
    ) {
        // Buscar si el rol existe
        Rol actual = service.buscarPorId(idRol);

        if (actual == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol no existe."
            );
        }

        // Validar si el nuevo nombre ya existe (y no es el mismo rol)
        if (service.existeRol(rol) && !actual.getRol().equalsIgnoreCase(rol)) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un rol con ese nombre."
            );
        }

        // Actualizar valores
        actual.setRol(rol);
        actual.setClasificacion(clasificacion);
        actual.setEstadoRol(estadoRol);

        service.guardarRol(actual);

        return Map.of(
                "success", true,
                "message", "Rol actualizado correctamente."
        );
    }

    /* Forma Tradicional
    @GetMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable Integer id) {
        service.eliminarRol(id);
        return "redirect:/roles/home";
    }*/
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> eliminarRol(@RequestParam Integer id) {

        try {
            service.eliminarRol(id);

            return Map.of(
                    "success", true,
                    "message", "Rol eliminado correctamente."
            );

        } catch (DataIntegrityViolationException e) {
            return Map.of(
                    "success", false,
                    "code", "FK_CONSTRAINT",
                    "message", "No es posible eliminar este rol porque está siendo usado por otros registros."
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al eliminar el rol: " + e.getMessage()
            );
        }
    }
}
