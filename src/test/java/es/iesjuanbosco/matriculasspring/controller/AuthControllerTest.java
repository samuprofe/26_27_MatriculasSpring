package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioRequest;
import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.exception.EmailRepetidoExpepcion;
import es.iesjuanbosco.matriculasspring.exception.GlobalExceptionHandler;
import es.iesjuanbosco.matriculasspring.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    private AuthService service;
    private AuthController controller;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AuthService.class);
        controller = new AuthController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void delegatesRegistrationAndReturnsCreatedResponse() {
        var request = UsuarioRequest.builder().email("persona@example.com").nombre("Persona").build();
        var expected = UsuarioResponse.builder().id("id-1").email(request.getEmail()).build();
        when(service.register(request)).thenReturn(expected);

        var response = controller.register(request);

        assertEquals(201, response.getStatusCode().value());
        assertSame(expected, response.getBody());
        verify(service).register(request);
    }

    @Test
    void returnsCreatedResponseOverHttp() throws Exception {
        var request = UsuarioRequest.builder().email("persona@example.com").nombre("Persona")
                .apellidos("Prueba").password("password123")
                .fechaNacimiento(LocalDate.of(2000, 1, 2)).build();
        when(service.register(request)).thenReturn(UsuarioResponse.builder().id("id-1")
                .email(request.getEmail()).nombre(request.getNombre()).apellidos(request.getApellidos()).build());

        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"persona@example.com","nombre":"Persona","apellidos":"Prueba",
                                 "password":"password123","fechaNacimiento":"2000-01-02"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {"id":"id-1","email":"persona@example.com","nombre":"Persona","apellidos":"Prueba"}
                        """))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(service).register(request);
    }

    @Test
    void duplicateEmailIsHandledAsConflictOverHttp() throws Exception {
        when(service.register(any())).thenThrow(new EmailRepetidoExpepcion());

        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"persona@example.com","nombre":"Persona","password":"password123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().json("{\"error\":\"El email ya está registrado\"}"));
        verify(service).register(any(UsuarioRequest.class));
    }

    @Test
    void validationRejectsInvalidRequestBeforeDelegating() throws Exception {
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"invalido","nombre":"","password":"corta","fechaNacimiento":"2999-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("El campo email debe ser un email válido"))
                .andExpect(jsonPath("$.nombre").value("El campo nombre no puede estar vacío"))
                .andExpect(jsonPath("$.password").value("La contraseña debe tener al menos 8 caracteres"))
                .andExpect(jsonPath("$.fechaNacimiento").value("La fecha de nacimiento debe ser anterior a la fecha actual"));
        verifyNoInteractions(service);
    }
}
