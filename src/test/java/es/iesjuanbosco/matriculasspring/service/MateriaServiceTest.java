package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.repository.MateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MateriaServiceTest {
    private MateriaRepository materias;
    private MateriaService service;

    @BeforeEach
    void setUp() {
        materias = mock(MateriaRepository.class);
        service = new MateriaService(materias);
    }

    @Test
    void crudResetsCreateIdAndChecksExistenceBeforeUpdateAndDelete() {
        Materia materia = new Materia();
        materia.setId(99L);
        List<Materia> list = List.of(materia);
        when(materias.findAll()).thenReturn(list);
        when(materias.findById(1L)).thenReturn(Optional.of(materia));
        when(materias.save(materia)).thenReturn(materia);
        when(materias.existsById(1L)).thenReturn(true);

        assertSame(list, service.findAll());
        assertSame(materia, service.findById(1L).orElseThrow());
        assertTrue(service.findById(2L).isEmpty());
        assertSame(materia, service.create(materia));
        assertNull(materia.getId());
        materia.setId(99L);
        assertTrue(service.update(2L, materia).isEmpty());
        assertEquals(99L, materia.getId());
        assertSame(materia, service.update(1L, materia).orElseThrow());
        assertEquals(1L, materia.getId());
        assertFalse(service.deleteById(2L));
        assertTrue(service.deleteById(1L));
        var order = inOrder(materias);
        order.verify(materias).findAll();
        order.verify(materias).findById(1L);
        order.verify(materias).findById(2L);
        order.verify(materias).save(materia);
        order.verify(materias).existsById(2L);
        order.verify(materias).existsById(1L);
        order.verify(materias).save(materia);
        order.verify(materias).existsById(2L);
        order.verify(materias).existsById(1L);
        order.verify(materias).deleteById(1L);
        verifyNoMoreInteractions(materias);
    }

    @Test
    void saveErrorsAreNotSwallowed() {
        Materia materia = new Materia();
        var failure = new IllegalStateException("save");
        when(materias.save(materia)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.create(materia)));
        verify(materias).save(materia);
        verifyNoMoreInteractions(materias);
    }
}
