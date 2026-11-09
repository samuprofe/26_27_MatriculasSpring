package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {
    private UsuarioService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(UsuarioService.class);
        mvc = MockMvcBuilders.standaloneSetup(new UsuarioController(service)).build();
    }

    @Test
    void listAndSearchKeepTheirDifferentResponseFormats() throws Exception {
        var response = UsuarioResponse.builder().id("u1").nombre("Ana").email("a@example.org").build();
        var usuario = Usuario.builder().id("u1").nombre("Ana").password("secret").build();
        when(service.getUsuarios()).thenReturn(List.of(response));
        when(service.buscarUsuariosPorNombre("an")).thenReturn(List.of(usuario));
        mvc.perform(get("/usuarios")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("u1"))
                .andExpect(jsonPath("$[0].email").value("a@example.org"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
        mvc.perform(get("/usuarios/buscar").param("nombre", "an"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].password").value("secret"));
        mvc.perform(get("/usuarios/buscar")).andExpect(status().isBadRequest());
        verify(service).getUsuarios();
        verify(service).buscarUsuariosPorNombre("an");
        verifyNoMoreInteractions(service);
    }

    @Test
    void createsAndFindsDetailAndReturnsNotFound() throws Exception {
        var saved = DetalleUsuario.builder().id(1L).calle("Mayor").build();
        when(service.createDetalleUsuario(eq("u1"), any(DetalleUsuario.class))).thenReturn(Optional.of(saved));
        when(service.findDetalleUsuario("u1")).thenReturn(Optional.of(saved));
        mvc.perform(post("/usuarios/u1/detalle-usuario").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":99,\"calle\":\"Mayor\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(post("/usuarios/missing/detalle-usuario").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calle\":\"Mayor\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/usuarios/u1/detalle-usuario"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.calle").value("Mayor"));
        mvc.perform(get("/usuarios/missing/detalle-usuario")).andExpect(status().isNotFound());
        var captor = ArgumentCaptor.forClass(DetalleUsuario.class);
        verify(service).createDetalleUsuario(eq("u1"), captor.capture());
        assertEquals(99L, captor.getValue().getId());
        assertEquals("Mayor", captor.getValue().getCalle());
        verify(service).createDetalleUsuario(eq("missing"), any(DetalleUsuario.class));
        verify(service).findDetalleUsuario("u1");
        verify(service).findDetalleUsuario("missing");
        verifyNoMoreInteractions(service);
    }

    @Test
    void deletesDetailAndReturnsNotFound() throws Exception {
        when(service.deleteDetalleUsuario("u1")).thenReturn(true);
        mvc.perform(delete("/usuarios/u1/detalle-usuario")).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(delete("/usuarios/missing/detalle-usuario")).andExpect(status().isNotFound());
        verify(service).deleteDetalleUsuario("u1");
        verify(service).deleteDetalleUsuario("missing");
        verifyNoMoreInteractions(service);
    }
}
