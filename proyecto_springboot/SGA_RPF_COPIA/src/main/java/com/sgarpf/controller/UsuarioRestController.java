package com.sgarpf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sgarpf.model.Usuario;
import com.sgarpf.service.UsuarioService;

@CrossOrigin(origins = {
	    "http://localhost:8080",
	    "http://127.0.0.1:8080",
	    "http://localhost:8082",
	    "http://127.0.0.1:8082",
        "https://sgarpf-production.up.railway.app",
        "https://dialmorx-wq.github.io"
	})

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    // LISTAR USUARIOS
    @GetMapping
    public List<Usuario> listarUsuarios() {

        return usuarioService.listarUsuarios();
    }

    // BUSCAR USUARIO POR ID
    @GetMapping("/{id}")
    public Usuario obtenerUsuario(@PathVariable Long id) {

        return usuarioService.obtenerUsuarioPorId(id);
    }

    // GUARDAR USUARIO
    @PostMapping
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {

        return usuarioService.guardarUsuario(usuario);
    }

    // ACTUALIZAR USUARIO
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        usuario.setId(id);

        return usuarioService.guardarUsuario(usuario);
    }

    // ELIMINAR USUARIO
    @DeleteMapping("/{id}")
    public String eliminarUsuario(@PathVariable Long id) {

        usuarioService.eliminarUsuario(id);

        return "Usuario eliminado correctamente";
    }
}