package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.service.AlumnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlumnoControllerTest {
    private AlumnoService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AlumnoService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AlumnoController(service)).build();
    }

    @Test
    void listAndLookupReturnExpectedStatusesAndBodies() throws Exception {
        var alumno = Alumno.builder().id(1L).nombre("Ana").build();
        when(service.findAll()).thenReturn(List.of(alumno));
        when(service.findById(1L)).thenReturn(Optional.of(alumno));
        mvc.perform(get("/alumnos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana"));
        mvc.perform(get("/alumnos/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        mvc.perform(get("/alumnos/2")).andExpect(status().isNotFound());
        verify(service).findAll();
        verify(service).findById(1L);
        verify(service).findById(2L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void createAndUpdateDelegateRequestAndReturnPersistedValues() throws Exception {
        var saved = Alumno.builder().id(1L).nombre("Ana").build();
        when(service.create(any(Alumno.class))).thenReturn(saved);
        when(service.update(eq(1L), any(Alumno.class))).thenReturn(Optional.of(saved));
        mvc.perform(post("/alumnos").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Ana\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/alumnos/1").contentType(MediaType.APPLICATION_JSON).content("{\"id\":99,\"nombre\":\"Ana\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/alumnos/2").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Ana\"}"))
                .andExpect(status().isNotFound());
        var captor = ArgumentCaptor.forClass(Alumno.class);
        verify(service).create(captor.capture());
        assertEquals("Ana", captor.getValue().getNombre());
        verify(service).update(eq(1L), captor.capture());
        assertEquals(99L, captor.getValue().getId());
        verify(service).update(eq(2L), any(Alumno.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    void scholarshipAndDeleteDelegateWithOriginalStatuses() throws Exception {
        var alumno = Alumno.builder().id(1L).build();
        alumno.setImporteBeca(new BigDecimal("25.50"));
        when(service.modifyImporteBeca(eq(1L), any(BigDecimal.class))).thenReturn(Optional.of(alumno));
        mvc.perform(patch("/alumnos/1/importe-beca").contentType(MediaType.APPLICATION_JSON).content("25.50"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(patch("/alumnos/2/importe-beca").contentType(MediaType.APPLICATION_JSON).content("25.50"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/alumnos/2")).andExpect(status().isNoContent());
        verify(service).modifyImporteBeca(1L, new BigDecimal("25.50"));
        verify(service).modifyImporteBeca(2L, new BigDecimal("25.50"));
        verify(service).deleteById(2L);
        verifyNoMoreInteractions(service);
    }
}
