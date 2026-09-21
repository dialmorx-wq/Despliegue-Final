package com.sgarpf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import com.sgarpf.model.Usuario;
import com.sgarpf.repository.UsuarioRepository;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // MOSTRAR LOGIN
    @GetMapping("/login")
    public String mostrarLogin() {

        return "login";
    }

    // VALIDAR LOGIN
    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        // BUSCAR USUARIO
        Usuario usuario =
                usuarioRepository.findByCorreo(correo);

        // VALIDAR LOGIN
        if (usuario != null &&
            usuario.getPassword().equals(password)) {

            // GUARDAR USUARIO EN SESION
            session.setAttribute("usuarioLogueado", usuario);

            // REDIRECCIONAR AL DASHBOARD
            return "redirect:/dashboard";
        }

        // ERROR LOGIN
        model.addAttribute("error", true);

        return "login";
    }

    // CERRAR SESION
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        // ELIMINAR SESION
        session.invalidate();

        return "redirect:/login";
    }

}