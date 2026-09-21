package com.sgarpf.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgarpf.model.Usuario;
import com.sgarpf.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerVolumenCrudTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    @Test
    void listarUsuariosVolumenConMilRegistros() {
        int registrosEsperados = 1000;
        List<Usuario> usuarios = new ArrayList<>();

        for (long id = 1; id <= registrosEsperados; id++) {
            usuarios.add(new Usuario(
                    id,
                    "Usuario " + id,
                    "usuario" + id + "@test.com",
                    "1234",
                    "USUARIO"));
        }

        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioRestController.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(registrosEsperados, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(1000L, resultado.get(999).getId());
        verify(usuarioService).listarUsuarios();
    }

    @Test
    void guardarUsuariosVolumenConMilRegistros() {
        int registrosEsperados = 1000;

        when(usuarioService.guardarUsuario(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            return usuario;
        });

        for (long id = 1; id <= registrosEsperados; id++) {
            Usuario usuario = new Usuario(
                    id,
                    "Usuario Nuevo " + id,
                    "nuevo" + id + "@test.com",
                    "1234",
                    "USUARIO");

            Usuario resultado = usuarioRestController.guardarUsuario(usuario);

            assertNotNull(resultado);
            assertEquals(id, resultado.getId());
            assertEquals("nuevo" + id + "@test.com", resultado.getCorreo());
        }

        verify(usuarioService, times(registrosEsperados)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void actualizarUsuariosVolumenConMilRegistros() {
        int registrosEsperados = 1000;

        when(usuarioService.guardarUsuario(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            return usuario;
        });

        for (long id = 1; id <= registrosEsperados; id++) {
            Usuario usuario = new Usuario(
                    null,
                    "Usuario Actualizado " + id,
                    "actualizado" + id + "@test.com",
                    "1234",
                    "ADMIN");

            Usuario resultado = usuarioRestController.actualizarUsuario(id, usuario);

            assertNotNull(resultado);
            assertEquals(id, usuario.getId());
            assertEquals(id, resultado.getId());
            assertEquals("ADMIN", resultado.getRol());
        }

        verify(usuarioService, times(registrosEsperados)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void eliminarUsuariosVolumenConMilRegistros() {
        int registrosEsperados = 1000;

        for (long id = 1; id <= registrosEsperados; id++) {
            String resultado = usuarioRestController.eliminarUsuario(id);

            assertEquals("Usuario eliminado correctamente", resultado);
        }

        verify(usuarioService, times(registrosEsperados)).eliminarUsuario(anyLong());
    }
}
