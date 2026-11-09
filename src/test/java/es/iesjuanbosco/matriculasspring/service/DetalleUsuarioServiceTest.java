package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.repository.DetalleUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DetalleUsuarioServiceTest {
    private DetalleUsuarioRepository repository;
    private DetalleUsuarioService service;

    @BeforeEach
    void setUp() {
        repository = mock(DetalleUsuarioRepository.class);
        service = new DetalleUsuarioService(repository);
    }

    @Test
    void listsAndFinds() {
        var detalle = DetalleUsuario.builder().id(1L).build();
        var list = List.of(detalle);
        when(repository.findAll()).thenReturn(list);
        when(repository.findById(1L)).thenReturn(Optional.of(detalle));
        assertSame(list, service.findAll());
        assertSame(detalle, service.findById(1L).orElseThrow());
        assertTrue(service.findById(2L).isEmpty());
        verify(repository).findAll();
        verify(repository).findById(1L);
        verify(repository).findById(2L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void createClearsIdAndUpdateUsesPathIdOnlyWhenFound() {
        var detalle = DetalleUsuario.builder().id(99L).build();
        when(repository.save(detalle)).thenAnswer(invocation -> detalle);
        when(repository.existsById(1L)).thenReturn(true);
        assertSame(detalle, service.create(detalle));
        assertNull(detalle.getId());
        detalle.setId(99L);
        assertSame(detalle, service.update(1L, detalle).orElseThrow());
        assertEquals(1L, detalle.getId());
        detalle.setId(99L);
        assertTrue(service.update(2L, detalle).isEmpty());
        assertEquals(99L, detalle.getId());
        var order = inOrder(repository);
        order.verify(repository).save(detalle);
        order.verify(repository).existsById(1L);
        order.verify(repository).save(detalle);
        order.verify(repository).existsById(2L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteChecksExistenceAndPropagatesFailures() {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.existsById(3L)).thenReturn(true);
        var failure = new IllegalStateException("failure");
        doThrow(failure).when(repository).deleteById(3L);
        assertTrue(service.deleteById(1L));
        assertFalse(service.deleteById(2L));
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.deleteById(3L)));
        var order = inOrder(repository);
        order.verify(repository).existsById(1L);
        order.verify(repository).deleteById(1L);
        order.verify(repository).existsById(2L);
        order.verify(repository).existsById(3L);
        order.verify(repository).deleteById(3L);
        verifyNoMoreInteractions(repository);
    }
}
