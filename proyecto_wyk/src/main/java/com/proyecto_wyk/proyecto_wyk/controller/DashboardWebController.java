package com.proyecto_wyk.proyecto_wyk.controller;

import com.proyecto_wyk.proyecto_wyk.security.CustomUserDetails;
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

        // 2. Verificar que el usuario esté autenticado y que sea de nuestro tipo CustomUserDetails
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 3. Acceder al objeto Usuario subyacente
            // (Asumiendo que CustomUserDetails tiene un método para acceder al objeto Usuario,
            // si no, usar los getters que implementaste en CustomUserDetails)
            // Ya que solo tienes un campo 'usuario' privado, usaremos los getters de CustomUserDetails:

            // Obtener los datos directamente de CustomUserDetails (o crear getters en CustomUserDetails si es necesario)
            String nombre = userDetails.getUsuario().getNombre();
            String email = userDetails.getUsername(); // Implementado como email en CustomUserDetails
            String telefono = String.valueOf(userDetails.getUsuario().getTelUsuario());
            String rol = userDetails.getAuthorities().iterator().next().getAuthority(); // El rol principal

            // Lógica para iniciales (ej: "John Doe" -> "JD")
            String[] partesNombre = nombre.split(" ");
            String iniciales = partesNombre[0].substring(0, 1) + (partesNombre.length > 1 ? partesNombre[1].substring(0, 1) : "");

            // 4. Inyectar los datos en el modelo de Thymeleaf
            model.addAttribute("userName", nombre);
            model.addAttribute("userEmail", email);
            model.addAttribute("userTel", telefono);
            model.addAttribute("userRol", rol);
            model.addAttribute("userInitials", iniciales.toUpperCase());

        } else {
            // Si no está autenticado, redirigir al login (Spring Security ya debería manejar esto)
            return "redirect:/login";
        }

        return "dashboard/dashboard"; // El nombre de tu plantilla (asumiendo que está en templates/dashboard/dashboard.html)
    }
}
