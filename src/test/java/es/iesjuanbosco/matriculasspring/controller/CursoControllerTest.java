package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.service.CursoService;
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

class CursoControllerTest {
    private CursoService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(CursoService.class);
        mvc = MockMvcBuilders.standaloneSetup(new CursoController(service)).build();
    }

    @Test
    void crudRoutesPreserveStatusesBodiesAndRequestIds() throws Exception {
        var saved = Curso.builder().id(1L).nombre("Java").build();
        when(service.findAll()).thenReturn(List.of(saved));
        when(service.findById(1L)).thenReturn(Optional.of(saved));
        when(service.create(any(Curso.class))).thenReturn(saved);
        when(service.update(eq(1L), any(Curso.class))).thenReturn(Optional.of(saved));
        when(service.deleteById(1L)).thenReturn(true);
        mvc.perform(get("/cursos")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Java"));
        mvc.perform(get("/cursos/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(get("/cursos/2")).andExpect(status().isNotFound());
        mvc.perform(post("/cursos").contentType(MediaType.APPLICATION_JSON).content("{\"id\":99,\"nombre\":\"Java\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/cursos/1").contentType(MediaType.APPLICATION_JSON).content("{\"id\":99,\"nombre\":\"Java\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/cursos/2").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/cursos/1")).andExpect(status().isNoContent());
        mvc.perform(delete("/cursos/2")).andExpect(status().isNotFound());
        mvc.perform(delete("/cursos")).andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(Curso.class);
        verify(service).findAll();
        verify(service).findById(1L);
        verify(service).findById(2L);
        verify(service).create(captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(1L), captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(2L), any(Curso.class));
        verify(service).deleteById(1L);
        verify(service).deleteById(2L);
        verify(service).deleteAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void relationRoutesDelegateAndMapSuccessAndNotFound() throws Exception {
        when(service.addMateria(1L, 2L)).thenReturn(Optional.of(Curso.builder().id(1L).build()));
        when(service.removeMateria(1L, 2L)).thenReturn(true);
        mvc.perform(post("/cursos/1/materias/2")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(post("/cursos/3/materias/2")).andExpect(status().isNotFound());
        mvc.perform(delete("/cursos/1/materias/2")).andExpect(status().isNoContent());
        mvc.perform(delete("/cursos/1/materias/3")).andExpect(status().isNotFound());
        verify(service).addMateria(1L, 2L);
        verify(service).addMateria(3L, 2L);
        verify(service).removeMateria(1L, 2L);
        verify(service).removeMateria(1L, 3L);
        verifyNoMoreInteractions(service);
    }
}
