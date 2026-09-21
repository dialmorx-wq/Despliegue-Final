package com.sgarpf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sgarpf.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        // VALIDAR SESION
        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogueado");

        // SI NO HAY SESION
        if(usuario == null) {

            return "redirect:/login";
        }

        // ENVIAR USUARIO A LA VISTA
        model.addAttribute("usuario", usuario);

        return "dashboard";
    }

}