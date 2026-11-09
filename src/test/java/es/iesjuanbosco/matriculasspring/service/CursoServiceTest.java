package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import es.iesjuanbosco.matriculasspring.repository.MateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CursoServiceTest {
    private CursoRepository cursos;
    private MateriaRepository materias;
    private CursoService service;

    @BeforeEach
    void setUp() {
        cursos = mock(CursoRepository.class);
        materias = mock(MateriaRepository.class);
        service = new CursoService(cursos, materias);
    }

    @Test
    void crudPreservesIdsChecksExistenceAndDelegates() {
        Curso curso = Curso.builder().id(99L).nombre("Java").build();
        List<Curso> list = List.of(curso);
        when(cursos.findAll()).thenReturn(list);
        when(cursos.findById(1L)).thenReturn(Optional.of(curso));
        when(cursos.save(curso)).thenReturn(curso);
        when(cursos.existsById(1L)).thenReturn(true);

        assertSame(list, service.findAll());
        assertSame(curso, service.findById(1L).orElseThrow());
        assertTrue(service.findById(2L).isEmpty());
        assertSame(curso, service.create(curso));
        assertEquals(99L, curso.getId());
        assertTrue(service.update(2L, curso).isEmpty());
        assertEquals(99L, curso.getId());
        assertSame(curso, service.update(1L, curso).orElseThrow());
        assertEquals(1L, curso.getId());
        assertFalse(service.deleteById(2L));
        assertTrue(service.deleteById(1L));
        service.deleteAll();
        var order = inOrder(cursos);
        order.verify(cursos).findAll();
        order.verify(cursos).findById(1L);
        order.verify(cursos).findById(2L);
        order.verify(cursos).save(curso);
        order.verify(cursos).existsById(2L);
        order.verify(cursos).existsById(1L);
        order.verify(cursos).save(curso);
        order.verify(cursos).existsById(2L);
        order.verify(cursos).existsById(1L);
        order.verify(cursos).deleteById(1L);
        order.verify(cursos).deleteAll();
        verifyNoMoreInteractions(cursos);
        verifyNoInteractions(materias);
    }

    @Test
    void addingAndRemovingUpdatesBothSidesAndSavesEvenOnDuplicateAdd() {
        Curso curso = Curso.builder().id(1L).build();
        Materia materia = new Materia();
        materia.setId(2L);
        when(cursos.findById(1L)).thenReturn(Optional.of(curso));
        when(materias.findById(2L)).thenReturn(Optional.of(materia));
        when(cursos.save(curso)).thenReturn(curso);

        assertSame(curso, service.addMateria(1L, 2L).orElseThrow());
        assertSame(curso, service.addMateria(1L, 2L).orElseThrow());
        assertEquals(List.of(materia), curso.getMaterias());
        assertEquals(List.of(curso), materia.getCursos());
        assertTrue(service.removeMateria(1L, 2L));
        assertTrue(curso.getMaterias().isEmpty());
        assertTrue(materia.getCursos().isEmpty());
        assertFalse(service.removeMateria(1L, 2L));
        verify(cursos, times(4)).findById(1L);
        verify(materias, times(4)).findById(2L);
        verify(cursos, times(3)).save(curso);
        verifyNoMoreInteractions(cursos, materias);
    }

    @Test
    void missingEitherReferenceStillLooksUpBothAndNeverSaves() {
        Curso curso = Curso.builder().id(1L).build();
        Materia materia = new Materia();
        when(cursos.findById(1L)).thenReturn(Optional.empty(), Optional.of(curso),
                Optional.empty(), Optional.of(curso));
        when(materias.findById(2L)).thenReturn(Optional.of(materia), Optional.empty(),
                Optional.of(materia), Optional.empty());
        assertTrue(service.addMateria(1L, 2L).isEmpty());
        assertTrue(service.addMateria(1L, 2L).isEmpty());
        assertFalse(service.removeMateria(1L, 2L));
        assertFalse(service.removeMateria(1L, 2L));
        var order = inOrder(cursos, materias);
        order.verify(cursos).findById(1L);
        order.verify(materias).findById(2L);
        order.verify(cursos).findById(1L);
        order.verify(materias).findById(2L);
        order.verify(cursos).findById(1L);
        order.verify(materias).findById(2L);
        order.verify(cursos).findById(1L);
        order.verify(materias).findById(2L);
        verifyNoMoreInteractions(cursos, materias);
    }

    @Test
    void repositoryExceptionsPropagateWithoutFurtherCalls() {
        var failure = new IllegalStateException("lookup");
        when(cursos.findById(1L)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.addMateria(1L, 2L)));
        verify(cursos).findById(1L);
        verifyNoMoreInteractions(cursos);
        verifyNoInteractions(materias);
    }
}
