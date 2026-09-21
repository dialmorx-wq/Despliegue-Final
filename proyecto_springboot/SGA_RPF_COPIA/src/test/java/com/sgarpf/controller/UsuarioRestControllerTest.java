package com.sgarpf.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgarpf.model.Usuario;
import com.sgarpf.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    @Test
    void listarUsuariosRetornaListaDelServicio() {
        List<Usuario> usuarios = List.of(
                new Usuario(1L, "Juliet Nieto", "jsnietop@gmail.com", "0000", "ADMIN"));

        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioRestController.listarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("ADMIN", resultado.get(0).getRol());
        verify(usuarioService).listarUsuarios();
    }

    @Test
    void obtenerUsuarioRetornaUsuarioPorId() {
        Usuario usuario = new Usuario(2L, "Sena", "sena@gmail.com", "0000", "USUARIO");
        when(usuarioService.obtenerUsuarioPorId(2L)).thenReturn(usuario);

        Usuario resultado = usuarioRestController.obtenerUsuario(2L);

        assertEquals("sena@gmail.com", resultado.getCorreo());
        verify(usuarioService).obtenerUsuarioPorId(2L);
    }

    @Test
    void guardarUsuarioRetornaUsuarioCreado() {
        Usuario usuario = new Usuario(null, "Nuevo", "nuevo@test.com", "1234", "USUARIO");
        Usuario creado = new Usuario(7L, "Nuevo", "nuevo@test.com", "1234", "USUARIO");

        when(usuarioService.guardarUsuario(usuario)).thenReturn(creado);

        Usuario resultado = usuarioRestController.guardarUsuario(usuario);

        assertEquals(7L, resultado.getId());
        verify(usuarioService).guardarUsuario(usuario);
    }

    @Test
    void actualizarUsuarioAsignaIdDeLaRutaAntesDeGuardar() {
        Usuario usuario = new Usuario(null, "Actualizado", "actualizado@test.com", "1234", "ADMIN");
        Usuario actualizado = new Usuario(15L, "Actualizado", "actualizado@test.com", "1234", "ADMIN");

        when(usuarioService.guardarUsuario(usuario)).thenReturn(actualizado);

        Usuario resultado = usuarioRestController.actualizarUsuario(15L, usuario);

        assertEquals(15L, usuario.getId());
        assertEquals(15L, resultado.getId());
        verify(usuarioService).guardarUsuario(usuario);
    }

    @Test
    void eliminarUsuarioRetornaMensajeCorrecto() {
        String resultado = usuarioRestController.eliminarUsuario(8L);

        assertEquals("Usuario eliminado correctamente", resultado);
        verify(usuarioService).eliminarUsuario(8L);
    }
}
