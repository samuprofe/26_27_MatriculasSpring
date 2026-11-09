package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.service.MateriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MateriaControllerTest {
    private MateriaService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(MateriaService.class);
        mvc = MockMvcBuilders.standaloneSetup(new MateriaController(service)).build();
    }

    @Test
    void crudRoutesPreserveStatusesBodiesAndDelegateRequests() throws Exception {
        Materia saved = new Materia();
        saved.setId(1L);
        saved.setNombre("Matemáticas");
        when(service.findAll()).thenReturn(List.of(saved));
        when(service.findById(1L)).thenReturn(Optional.of(saved));
        when(service.create(any(Materia.class))).thenReturn(saved);
        when(service.update(eq(1L), any(Materia.class))).thenReturn(Optional.of(saved));
        when(service.deleteById(1L)).thenReturn(true);
        mvc.perform(get("/materias")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Matemáticas"));
        mvc.perform(get("/materias/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(get("/materias/2")).andExpect(status().isNotFound());
        mvc.perform(post("/materias").contentType(MediaType.APPLICATION_JSON).content("{\"id\":99,\"nombre\":\"Matemáticas\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/materias/1").contentType(MediaType.APPLICATION_JSON).content("{\"id\":99,\"nombre\":\"Matemáticas\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/materias/2").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/materias/1")).andExpect(status().isNoContent());
        mvc.perform(delete("/materias/2")).andExpect(status().isNotFound());
        var captor = ArgumentCaptor.forClass(Materia.class);
        verify(service).findAll();
        verify(service).findById(1L);
        verify(service).findById(2L);
        verify(service).create(captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(1L), captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(2L), any(Materia.class));
        verify(service).deleteById(1L);
        verify(service).deleteById(2L);
        verifyNoMoreInteractions(service);
    }
}
