package com.sgarpf.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgarpf.model.Usuario;
import com.sgarpf.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class LoginRestControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LoginRestController loginRestController;

    @Test
    void iniciarSesionConCredencialesValidasRetornaUsuarioSinPassword() {
        Usuario datosLogin = new Usuario(null, null, "jsnietop@gmail.com", "0000", null);
        Usuario usuarioEncontrado = new Usuario(2L, "Juliet Nieto", "jsnietop@gmail.com", "0000", "ADMIN");

        when(usuarioRepository.findByCorreo("jsnietop@gmail.com")).thenReturn(usuarioEncontrado);

        Usuario resultado = loginRestController.iniciarSesion(datosLogin);

        assertEquals(2L, resultado.getId());
        assertEquals("ADMIN", resultado.getRol());
        assertNull(resultado.getPassword());
        verify(usuarioRepository).findByCorreo("jsnietop@gmail.com");
    }

    @Test
    void iniciarSesionConPasswordIncorrectoLanzaExcepcion() {
        Usuario datosLogin = new Usuario(null, null, "jsnietop@gmail.com", "bad", null);
        Usuario usuarioEncontrado = new Usuario(2L, "Juliet Nieto", "jsnietop@gmail.com", "0000", "ADMIN");

        when(usuarioRepository.findByCorreo("jsnietop@gmail.com")).thenReturn(usuarioEncontrado);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> loginRestController.iniciarSesion(datosLogin));

        assertEquals("Correo o contraseña incorrectos", exception.getMessage());
        verify(usuarioRepository).findByCorreo("jsnietop@gmail.com");
    }

    @Test
    void iniciarSesionConCorreoInexistenteLanzaExcepcion() {
        Usuario datosLogin = new Usuario(null, null, "nadie@test.com", "1234", null);

        when(usuarioRepository.findByCorreo("nadie@test.com")).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> loginRestController.iniciarSesion(datosLogin));

        assertEquals("Correo o contraseña incorrectos", exception.getMessage());
        verify(usuarioRepository).findByCorreo("nadie@test.com");
    }
}
