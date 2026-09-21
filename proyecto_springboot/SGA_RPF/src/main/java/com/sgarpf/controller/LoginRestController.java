package com.sgarpf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sgarpf.model.Usuario;
import com.sgarpf.repository.UsuarioRepository;

@CrossOrigin(origins = {
	    "http://localhost:8080",
	    "http://127.0.0.1:8080",
	    "http://localhost:8082",
	    "http://127.0.0.1:8082"
	})
	@RestController
	@RequestMapping("/api/auth")
	public class LoginRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public Usuario iniciarSesion(@RequestBody Usuario datosLogin) {

        Usuario usuario = usuarioRepository.findByCorreo(datosLogin.getCorreo());

        if (usuario != null &&
            usuario.getPassword().equals(datosLogin.getPassword())) {

            usuario.setPassword(null);
            return usuario;
        }

        throw new RuntimeException("Correo o contraseña incorrectos");
    }
}