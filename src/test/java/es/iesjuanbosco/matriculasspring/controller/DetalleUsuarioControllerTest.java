package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.service.DetalleUsuarioService;
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

class DetalleUsuarioControllerTest {
    private DetalleUsuarioService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(DetalleUsuarioService.class);
        mvc = MockMvcBuilders.standaloneSetup(new DetalleUsuarioController(service)).build();
    }

    @Test
    void listsAndFindsDetails() throws Exception {
        var detalle = DetalleUsuario.builder().id(1L).calle("Mayor").build();
        when(service.findAll()).thenReturn(List.of(detalle));
        when(service.findById(1L)).thenReturn(Optional.of(detalle));
        mvc.perform(get("/detalles-usuarios")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calle").value("Mayor"));
        mvc.perform(get("/detalles-usuarios/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        mvc.perform(get("/detalles-usuarios/2")).andExpect(status().isNotFound());
        mvc.perform(get("/detalles-usuarios/no-numero")).andExpect(status().isBadRequest());
        verify(service).findAll();
        verify(service).findById(1L);
        verify(service).findById(2L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void createsAndUpdatesOrReturnsNotFound() throws Exception {
        var saved = DetalleUsuario.builder().id(1L).calle("Mayor").build();
        when(service.create(any(DetalleUsuario.class))).thenReturn(saved);
        when(service.update(eq(1L), any(DetalleUsuario.class))).thenReturn(Optional.of(saved));
        mvc.perform(post("/detalles-usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":99,\"calle\":\"Mayor\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/detalles-usuarios/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":99,\"calle\":\"Mayor\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/detalles-usuarios/2").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calle\":\"Mayor\"}"))
                .andExpect(status().isNotFound());
        var captor = ArgumentCaptor.forClass(DetalleUsuario.class);
        verify(service).create(captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(1L), captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(2L), any(DetalleUsuario.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    void deletesOrReturnsNotFound() throws Exception {
        when(service.deleteById(1L)).thenReturn(true);
        mvc.perform(delete("/detalles-usuarios/1")).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(delete("/detalles-usuarios/2")).andExpect(status().isNotFound());
        verify(service).deleteById(1L);
        verify(service).deleteById(2L);
        verifyNoMoreInteractions(service);
    }
}
