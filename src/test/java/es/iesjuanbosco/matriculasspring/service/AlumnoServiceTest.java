package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlumnoServiceTest {
    private AlumnoRepository repository;
    private AlumnoService service;

    @BeforeEach
    void setUp() {
        repository = mock(AlumnoRepository.class);
        service = new AlumnoService(repository);
    }

    @Test
    void queriesAndCreatesUsingRepositoryResults() {
        var alumno = Alumno.builder().id(1L).build();
        var list = List.of(alumno);
        when(repository.findAll()).thenReturn(list);
        when(repository.findById(1L)).thenReturn(Optional.of(alumno));
        when(repository.save(alumno)).thenReturn(alumno);

        assertSame(list, service.findAll());
        assertSame(alumno, service.findById(1L).orElseThrow());
        assertTrue(service.findById(2L).isEmpty());
        assertSame(alumno, service.create(alumno));
        verify(repository).findAll();
        verify(repository).findById(1L);
        verify(repository).findById(2L);
        verify(repository).save(alumno);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateUsesPathIdAndSkipsMissingAlumno() {
        var alumno = Alumno.builder().id(99L).build();
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(alumno)).thenReturn(alumno);

        assertSame(alumno, service.update(1L, alumno).orElseThrow());
        assertEquals(1L, alumno.getId());
        assertTrue(service.update(2L, alumno).isEmpty());
        var order = inOrder(repository);
        order.verify(repository).existsById(1L);
        order.verify(repository).save(alumno);
        order.verify(repository).existsById(2L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void scholarshipUpdateSavesOnlyExistingAlumno() {
        var alumno = Alumno.builder().id(1L).build();
        var amount = new BigDecimal("42.125");
        when(repository.findById(1L)).thenReturn(Optional.of(alumno));
        when(repository.save(alumno)).thenReturn(alumno);

        assertSame(alumno, service.modifyImporteBeca(1L, amount).orElseThrow());
        assertEquals(amount, alumno.getImporteBeca());
        assertTrue(service.modifyImporteBeca(2L, amount).isEmpty());
        verify(repository).findById(1L);
        verify(repository).findById(2L);
        verify(repository).save(alumno);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteDoesNotCheckExistenceAndPropagatesFailure() {
        var failure = new IllegalStateException("delete failed");
        doThrow(failure).when(repository).deleteById(2L);
        service.deleteById(1L);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.deleteById(2L)));
        verify(repository).deleteById(1L);
        verify(repository).deleteById(2L);
        verifyNoMoreInteractions(repository);
    }
}
