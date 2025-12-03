package com.proyecto_wyk.proyecto_wyk.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashboardWebController {
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        // 1. Obtener la información del usuario autenticado (Principal)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // El 'username' es el email, según tu CustomUserDetails.
        String userEmail = authentication.getName();

        // 2. Puedes añadir el nombre del usuario al modelo si lo obtuvieras de la BDD.
        // Por ahora, solo usaremos el email como marcador de posición.
        // Si necesitas el NOMBRE_USUARIO real, tendrías que inyectar el UsuarioRepository
        // y buscar el usuario por el email para obtener el nombre completo.

        // ****************************************************************************
        // NOTA: Para obtener el nombre real, necesitarías inyectar UsuarioRepository
        // y buscar el objeto Usuario usando userEmail.
        // Por simplicidad, usaremos el email como "nombre" por ahora, o lo dejamos vacío.
        // Para usar el nombre real, debes inyectar:
        // @Autowired private UsuarioRepository usuarioRepository;
        // Usuario usuario = usuarioRepository.findByEmailUsuario(userEmail).orElse(null);
        // model.addAttribute("userName", usuario != null ? usuario.getNombre() : userEmail);
        // ****************************************************************************

        // Usaremos el email como marcador hasta que implementes la inyección del repositorio:
        model.addAttribute("userName", userEmail);

        // 3. Devolver la plantilla
        return "dashboard/dashboard";
    }
}
