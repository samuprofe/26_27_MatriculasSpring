package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.entity.Matricula;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import es.iesjuanbosco.matriculasspring.repository.MatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MatriculaServiceTest {
    private MatriculaRepository matriculas;
    private AlumnoRepository alumnos;
    private CursoRepository cursos;
    private MatriculaService service;

    @BeforeEach
    void setUp() {
        matriculas = mock(MatriculaRepository.class);
        alumnos = mock(AlumnoRepository.class);
        cursos = mock(CursoRepository.class);
        service = new MatriculaService(matriculas, alumnos, cursos);
    }

    @Test
    void creationLoadsBothReferencesAndSavesNewEntityWithOriginalBehavior() {
        var alumno = Alumno.builder().id(1L).nombre("Ana").build();
        var curso = Curso.builder().id(2L).nombre("Java").build();
        var request = Matricula.builder().id(99L).alumno(Alumno.builder().id(1L).build())
                .curso(Curso.builder().id(2L).build()).cursoLectivo("2025").pagoSeguro(true).build();
        var saved = Matricula.builder().id(3L).build();
        when(alumnos.findById(1L)).thenReturn(Optional.of(alumno));
        when(cursos.findById(2L)).thenReturn(Optional.of(curso));
        when(matriculas.save(any(Matricula.class))).thenReturn(saved);

        assertSame(saved, service.create(request).orElseThrow());
        var captor = ArgumentCaptor.forClass(Matricula.class);
        var order = inOrder(alumnos, cursos, matriculas);
        order.verify(alumnos).findById(1L);
        order.verify(cursos).findById(2L);
        order.verify(matriculas).save(captor.capture());
        var nueva = captor.getValue();
        assertNotSame(request, nueva);
        assertSame(alumno, nueva.getAlumno());
        assertSame(curso, nueva.getCurso());
        assertNull(nueva.getId());
        assertNull(nueva.getCursoLectivo());
        assertFalse(nueva.isPagoSeguro());
        assertNull(nueva.getUsuario());
        verifyNoMoreInteractions(alumnos, cursos, matriculas);
    }

    @Test
    void creationChecksBothReferencesEvenWhenEitherIsMissing() {
        var request = Matricula.builder().alumno(Alumno.builder().id(1L).build())
                .curso(Curso.builder().id(2L).build()).build();
        when(cursos.findById(2L)).thenReturn(Optional.of(Curso.builder().id(2L).build()))
                .thenReturn(Optional.empty());
        when(alumnos.findById(1L)).thenReturn(Optional.empty())
                .thenReturn(Optional.of(Alumno.builder().id(1L).build()));

        assertTrue(service.create(request).isEmpty());
        assertTrue(service.create(request).isEmpty());
        verify(alumnos, times(2)).findById(1L);
        verify(cursos, times(2)).findById(2L);
        verifyNoInteractions(matriculas);
    }

    @Test
    void deleteChecksExistenceBeforeDeleting() {
        when(matriculas.existsById(1L)).thenReturn(true);
        assertTrue(service.deleteById(1L));
        assertFalse(service.deleteById(2L));
        var order = inOrder(matriculas);
        order.verify(matriculas).existsById(1L);
        order.verify(matriculas).deleteById(1L);
        order.verify(matriculas).existsById(2L);
        verifyNoMoreInteractions(matriculas);
    }

    @Test
    void filteredQueriesDistinguishMissingParentFromEmptyList() {
        var list = List.of(Matricula.builder().id(3L).build());
        when(cursos.existsById(1L)).thenReturn(true);
        when(alumnos.existsById(1L)).thenReturn(true);
        when(matriculas.findByCursoId(1L)).thenReturn(list);
        when(matriculas.findByAlumnoId(1L)).thenReturn(List.of());
        assertSame(list, service.findByCurso(1L).orElseThrow());
        assertTrue(service.findByAlumno(1L).orElseThrow().isEmpty());
        assertTrue(service.findByCurso(2L).isEmpty());
        assertTrue(service.findByAlumno(2L).isEmpty());
        verify(cursos).existsById(1L);
        verify(cursos).existsById(2L);
        verify(alumnos).existsById(1L);
        verify(alumnos).existsById(2L);
        verify(matriculas).findByCursoId(1L);
        verify(matriculas).findByAlumnoId(1L);
        verifyNoMoreInteractions(matriculas, alumnos, cursos);
    }

    @Test
    void allAndSummaryUseRepositoryAndMapFieldsInOrder() {
        var list = List.of(Matricula.builder().id(4L)
                .alumno(Alumno.builder().nombre("Ana").build())
                .curso(Curso.builder().nombre("Java").build()).build());
        when(matriculas.findAll()).thenReturn(list);
        assertSame(list, service.findAll());
        var resumen = service.getMatriculasResumen();
        assertEquals(1, resumen.size());
        assertEquals("Ana", resumen.get(0).getNombreAlumno());
        assertEquals("Java", resumen.get(0).getNombreCurso());
        assertEquals(4L, resumen.get(0).getIdMatricula());
        verify(matriculas, times(2)).findAll();
        verifyNoMoreInteractions(matriculas);
        verifyNoInteractions(alumnos, cursos);
    }

    @Test
    void lookupFailurePropagatesWithoutSaving() {
        var request = Matricula.builder().alumno(Alumno.builder().id(1L).build())
                .curso(Curso.builder().id(2L).build()).build();
        var failure = new IllegalStateException("lookup failed");
        when(alumnos.findById(1L)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.create(request)));
        verify(alumnos).findById(1L);
        verifyNoInteractions(cursos, matriculas);
    }
}
