package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioCreateDTO;
import com.proyecto_wyk.proyecto_wyk.dto.usuario.UsuarioUpdateDTO;
import com.proyecto_wyk.proyecto_wyk.entity.Rol;
import com.proyecto_wyk.proyecto_wyk.entity.Usuario;
import com.proyecto_wyk.proyecto_wyk.service.impl.RolService;
import com.proyecto_wyk.proyecto_wyk.service.impl.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final RolService rolService;
    // INYECTAMOS DEPENDENCIA PARA CIFRAR CONTRASEÑA
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, RolService rolService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

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

        // CIFRAR PASSWORD
        nuevoUsuario.setPasswordUsuario(passwordEncoder.encode(dto.getPasswordUsuario()));

        nuevoUsuario.setTelUsuario(Long.valueOf(dto.getTelUsuario()));
        nuevoUsuario.setEmailUsuario(dto.getEmailUsuario());
        nuevoUsuario.setFechaRegistro(java.time.LocalDateTime.now());
        nuevoUsuario.setEstadoUsuario(true);

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
        // SOLO ACTUALIZAR Y CIFRAR LA CONTRASEÑA SÍ SE PROPORCIONÓ UNA NUEVA
        if (dto.getPasswordUsuario() != null && !dto.getPasswordUsuario().isBlank()) {
            actualUsuario.setPasswordUsuario(passwordEncoder.encode(dto.getPasswordUsuario()));
        }
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
            usuarioService.eliminarUsuario(id);

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

    @GetMapping("/generateReportPDF")
    public ResponseEntity<InputStreamResource> generarPDF(@RequestParam Map<String, String> params) throws Exception {

        List<Usuario> listaUsuarios = usuarioService.listarUsuariosFiltrados(params);

        StringBuilder html = new StringBuilder();
        html.append("""
                    <html>
                    <head>
                    <style>
                        @page {
                            size: A4 landscape;
                            margin: 20mm;
                        }
                        table { width:100%; border-collapse: collapse; }
                        th, td { border:1px solid #ccc; padding:8px; }
                        th { background-color:#f2f2f2; }
                    </style>
                    </head>
                    <body>
                    <h2>Reporte de Usuarios</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>Número documento</th>
                                <th>Nombre</th>
                                <th>Teléfono</th>
                                <th>Email</th>
                                <th>Fecha Registro</th>
                                <th>Rol</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                """);

        for (Usuario u : listaUsuarios) {
            html.append("<tr>")
                    .append("<td>").append(u.getNumDoc()).append("</td>")
                    .append("<td>").append(u.getNombre()).append("</td>")
                    .append("<td>").append(u.getTelUsuario()).append("</td>")
                    .append("<td>").append(u.getEmailUsuario()).append("</td>")
                    .append("<td>").append(u.getFechaRegistro()).append("</td>")
                    .append("<td>").append(u.getRol().getRol()).append("</td>")
                    .append("<td>").append(u.isEstadoUsuario() ? "Activo" : "Inactivo").append("</td>")
                    .append("</tr>");
        }

        html.append("""
                    </tbody>
                    </table>
                    </body>
                    </html>
                """);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html.toString());
        renderer.layout();
        renderer.createPDF(os);

        ByteArrayInputStream pdfStream = new ByteArrayInputStream(os.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=usuarios.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}
