package com.proyecto_wyk.proyecto_wyk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {
    @GetMapping("/login")
    public String showLoginPage() {
        // Retorna la ubicación de la plantilla: templates/login/login.html
        return "login/login";
    }
}
