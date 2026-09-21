package com.sgarpf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByCorreo(String correo);

}