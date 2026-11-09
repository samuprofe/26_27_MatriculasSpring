package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.DTO.MatriculaResumenResponse;
import es.iesjuanbosco.matriculasspring.entity.Matricula;
import es.iesjuanbosco.matriculasspring.service.MatriculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MatriculaControllerTest {
    private MatriculaService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(MatriculaService.class);
        mvc = MockMvcBuilders.standaloneSetup(new MatriculaController(service)).build();
    }

    @Test
    void createReturnsCreatedOrNotFoundAndDelegatesReferences() throws Exception {
        when(service.create(any(Matricula.class))).thenReturn(Optional.of(Matricula.builder().id(3L).build()))
                .thenReturn(Optional.empty());
        String body = "{\"alumno\":{\"id\":1},\"curso\":{\"id\":2},\"cursoLectivo\":\"2025\",\"pagoSeguro\":true}";
        mvc.perform(post("/matriculas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3));
        mvc.perform(post("/matriculas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
        var captor = ArgumentCaptor.forClass(Matricula.class);
        verify(service, times(2)).create(captor.capture());
        assertEquals(1L, captor.getValue().getAlumno().getId());
        assertEquals(2L, captor.getValue().getCurso().getId());
        assertEquals("2025", captor.getValue().getCursoLectivo());
        assertTrue(captor.getValue().isPagoSeguro());
        verifyNoMoreInteractions(service);
    }

    @Test
    void invalidReferencesReturnBadRequestWithoutServiceCalls() throws Exception {
        for (String body : List.of("{\"pagoSeguro\":false}", "{\"alumno\":{},\"curso\":{\"id\":2},\"pagoSeguro\":false}",
                "{\"alumno\":{\"id\":1},\"pagoSeguro\":false}", "{\"alumno\":{\"id\":1},\"curso\":{},\"pagoSeguro\":false}")) {
            mvc.perform(post("/matriculas").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertNull(result.getResolvedException()));
        }
        verifyNoInteractions(service);
    }

    @Test
    void listSummaryAndFilteredListsKeepRoutesAndStatuses() throws Exception {
        when(service.findAll()).thenReturn(List.of(Matricula.builder().id(3L).build()));
        when(service.getMatriculasResumen()).thenReturn(List.of(MatriculaResumenResponse.builder()
                .idMatricula(3L).nombreAlumno("Ana").nombreCurso("Java").build()));
        when(service.findByCurso(1L)).thenReturn(Optional.of(List.of()));
        when(service.findByAlumno(1L)).thenReturn(Optional.of(List.of(Matricula.builder().id(3L).build())));
        mvc.perform(get("/matriculas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(3));
        mvc.perform(get("/matriculas/resumen")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreAlumno").value("Ana"))
                .andExpect(jsonPath("$[0].nombreCurso").value("Java"))
                .andExpect(jsonPath("$[0].idMatricula").value(3));
        mvc.perform(get("/cursos/1/matriculas")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/alumnos/1/matriculas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(3));
        mvc.perform(get("/cursos/2/matriculas")).andExpect(status().isNotFound());
        mvc.perform(get("/alumnos/2/matriculas")).andExpect(status().isNotFound());
        verify(service).findAll();
        verify(service).getMatriculasResumen();
        verify(service).findByCurso(1L);
        verify(service).findByCurso(2L);
        verify(service).findByAlumno(1L);
        verify(service).findByAlumno(2L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deleteReturnsNoContentOrNotFound() throws Exception {
        when(service.deleteById(1L)).thenReturn(true);
        mvc.perform(delete("/matriculas/1")).andExpect(status().isNoContent());
        mvc.perform(delete("/matriculas/2")).andExpect(status().isNotFound());
        verify(service).deleteById(1L);
        verify(service).deleteById(2L);
        verifyNoMoreInteractions(service);
    }
}
