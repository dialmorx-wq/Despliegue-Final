package com.sgarpf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.sgarpf.model.Usuario;
import com.sgarpf.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String inicio(Model model,
            HttpSession session) {

        // VALIDAR SESION DM
        if (session.getAttribute("usuarioLogueado") == null) {

            return "redirect:/login";
        }

        model.addAttribute("usuario", new Usuario());

        model.addAttribute(
                "listaUsuarios",
                usuarioService.listarUsuarios());

        return "usuarios";
    }
    
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {

        model.addAttribute("usuario", new Usuario());

        model.addAttribute(
                "listaUsuarios",
                usuarioService.listarUsuarios());

        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(
            @Valid Usuario usuario,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // VALIDACIONES
        if (result.hasErrors()) {

            model.addAttribute(
                    "listaUsuarios",
                    usuarioService.listarUsuarios());

            return "usuarios";
        }

        // VALIDAR SI ES NUEVO O EDICIÓN
        if (usuario.getId() == null) {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Usuario creado correctamente");

        } else {

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Usuario actualizado correctamente");
        }

        // GUARDAR
        usuarioService.guardarUsuario(usuario);

        return "redirect:/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        usuarioService.eliminarUsuario(id);

        redirectAttributes.addFlashAttribute(
                "danger",
                "Usuario eliminado correctamente");

        return "redirect:/";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        model.addAttribute("usuario", usuario);

        model.addAttribute(
                "listaUsuarios",
                usuarioService.listarUsuarios());

        return "usuarios";
    }
}	