package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.service.impl.RolService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // ================================
    // 🔵 DASHBOARD
    // ================================
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listaUsuarios", usuarioService.listarUsuario());
        model.addAttribute("usuariosExistentes", usuarioService.cantUsuariosExistentes());
        model.addAttribute("usuariosActivos", usuarioService.cantUsuariosActivos());
        model.addAttribute("usuariosInactivos", usuarioService.cantUsuariosInactivos());

        return "usuario/dashboardUsuario";
    }

    @GetMapping("/formGuardar")
    public String mostrarFormGuardar(Model model) {
        model.addAttribute("formGuardarUsuario", new UsuarioCreateDTO());
        model.addAttribute("listaRoles", rolService.listarRol());
        return "usuario/formGuardar";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarUsuario(
            @Valid @RequestBody UsuarioCreateDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return Map.of(
                    "success", false,
                    "message", result.getFieldError().getDefaultMessage()
            );
        }

        if (usuarioService.existeUsuario(Long.valueOf(dto.getNumDoc()))) {
            return Map.of(
                    "success", false,
                    "message", "Ya existe un usuario con ese número de documento."
            );
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNumDoc(Long.valueOf(dto.getNumDoc()));
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setPasswordUsuario(dto.getPasswordUsuario());
        nuevoUsuario.setTelUsuario(Long.valueOf(dto.getTelUsuario()));
        nuevoUsuario.setEmailUsuario(dto.getEmailUsuario());
        nuevoUsuario.setEstadoUsuario(true);
        nuevoUsuario.setFechaRegistro(java.time.LocalDateTime.now());

        // --- ASIGNAR ROL CORRECTAMENTE ---
        Rol rol = rolService.buscarPorId(dto.getRolId());
        if (rol == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol seleccionado no existe.");
        }
        nuevoUsuario.setRol(rol);

        usuarioService.guardarUsuario(nuevoUsuario);

        return Map.of(
                "success", true,
                "message", "Usuario registrado correctamente."
        );
    }

    @GetMapping("/formAct/{id}")
    public String mostrarFormActualizar(@PathVariable Long id, Model model) {

        Usuario usuario = usuarioService.buscarPorID(id);

        model.addAttribute("formActUsuario", usuario);
        model.addAttribute("listaRoles", rolService.listarRol());

        return "usuario/formActualizar";
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarUsuario(
            @Valid @RequestBody UsuarioUpdateDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            String mensaje = result.getFieldError().getDefaultMessage();
            return Map.of(
                    "success", false,
                    "message", mensaje
            );
        }

        Usuario actualUsuario = usuarioService.buscarPorID(dto.getIdUsuario());

        if (actualUsuario == null) {
            return Map.of(
                    "success", false,
                    "message", "El usuario no existe."
            );
        }

        // validar documento duplicado
        if (usuarioService.existeUsuario(Long.valueOf(dto.getNumDoc()))
                && !actualUsuario.getNumDoc().equals(Long.valueOf(dto.getNumDoc()))) {

            return Map.of(
                    "success", false,
                    "message", "Ya existe un usuario con ese número de documento."
            );
        }

        actualUsuario.setNumDoc(Long.valueOf(dto.getNumDoc()));
        actualUsuario.setNombre(dto.getNombre());
        actualUsuario.setPasswordUsuario(dto.getPasswordUsuario());
        actualUsuario.setTelUsuario(Long.valueOf(dto.getTelUsuario()));
        actualUsuario.setEmailUsuario(dto.getEmailUsuario());
        actualUsuario.setEstadoUsuario(dto.isEstadoUsuario());

        // --- ASIGNAR ROL CORRECTAMENTE ---
        Rol rol = rolService.buscarPorId(dto.getRolId());
        if (rol == null) {
            return Map.of(
                    "success", false,
                    "message", "El rol seleccionado no existe.");
        }
        actualUsuario.setRol(rol);

        usuarioService.guardarUsuario(actualUsuario);

        return Map.of(
                "success", true,
                "message", "Usuario actualizado correctamente."
        );
    }
    
    @PostMapping("/updateState")
    @ResponseBody
    public Map<String, Object> updateState(@RequestBody Map<String, Object> body) {
        try {
            Long id = Long.valueOf(body.get("id").toString());
            Integer estado = Integer.parseInt(body.get("estado").toString());

            boolean nuevoEstado = estado == 1;

            Usuario usuario = usuarioService.buscarPorID(id);

            if (usuario == null) {
                return Map.of(
                        "success", false,
                        "message", "El usuario no existe."
                );
            }

            usuario.setEstadoUsuario(nuevoEstado);
            usuarioService.guardarUsuario(usuario);

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
            usuarioService.eliminarRol(id);

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
