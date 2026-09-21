package com.sgarpf.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgarpf.model.Usuario;
import com.sgarpf.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void listarUsuariosRetornaUsuariosDelRepositorio() {
        List<Usuario> usuarios = List.of(
                new Usuario(1L, "Juliet Nieto", "jsnietop@gmail.com", "0000", "ADMIN"),
                new Usuario(2L, "Sena", "sena@gmail.com", "0000", "USUARIO"));

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(2, resultado.size());
        assertEquals("Juliet Nieto", resultado.get(0).getNombre());
        verify(usuarioRepository).findAll();
    }

    @Test
    void guardarUsuarioPersisteYRetornaUsuario() {
        Usuario usuario = new Usuario(null, "Diego", "diego@test.com", "1234", "ADMIN");
        Usuario usuarioGuardado = new Usuario(10L, "Diego", "diego@test.com", "1234", "ADMIN");

        when(usuarioRepository.save(usuario)).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertEquals(10L, resultado.getId());
        assertEquals("ADMIN", resultado.getRol());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void obtenerUsuarioPorIdRetornaUsuarioCuandoExiste() {
        Usuario usuario = new Usuario(1L, "Usuario Prueba", "usuario@test.com", "1234", "USUARIO");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertEquals("usuario@test.com", resultado.getCorreo());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerUsuarioPorIdRetornaNullCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.obtenerUsuarioPorId(99L);

        assertNull(resultado);
        verify(usuarioRepository).findById(99L);
    }

    @Test
    void eliminarUsuarioEliminaPorId() {
        usuarioService.eliminarUsuario(5L);

        verify(usuarioRepository).deleteById(5L);
    }
}
