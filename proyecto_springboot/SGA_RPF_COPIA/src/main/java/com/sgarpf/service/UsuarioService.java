package com.sgarpf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgarpf.model.Usuario;
import com.sgarpf.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // LISTAR USUARIOS
    public List<Usuario> listarUsuarios() {

        return usuarioRepository.findAll();
    }

    // GUARDAR USUARIO
    public Usuario guardarUsuario(Usuario usuario) {

        return usuarioRepository.save(usuario);
    }

    // ELIMINAR USUARIO
    public void eliminarUsuario(Long id) {

        usuarioRepository.deleteById(id);
    }

    // OBTENER USUARIO POR ID
    public Usuario obtenerUsuarioPorId(Long id) {

        return usuarioRepository.findById(id).orElse(null);
    }
}