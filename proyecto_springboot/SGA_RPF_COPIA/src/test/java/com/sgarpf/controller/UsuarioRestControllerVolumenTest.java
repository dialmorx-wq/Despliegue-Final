package com.sgarpf.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgarpf.model.Usuario;
import com.sgarpf.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerVolumenTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    @Test
    void listarPorIdVolumenConMilRegistros() {
        int registrosEsperados = 1000;

        for (long id = 1; id <= registrosEsperados; id++) {
            Usuario usuario = new Usuario(
                    id,
                    "Usuario " + id,
                    "usuario" + id + "@test.com",
                    "1234",
                    "USUARIO");

            when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(usuario);
        }

        for (long id = 1; id <= registrosEsperados; id++) {
            Usuario resultado = usuarioRestController.obtenerUsuario(id);

            assertNotNull(resultado);
            assertEquals(id, resultado.getId());
            assertEquals("usuario" + id + "@test.com", resultado.getCorreo());
        }

        verify(usuarioService, times(registrosEsperados)).obtenerUsuarioPorId(anyLong());
    }
}
