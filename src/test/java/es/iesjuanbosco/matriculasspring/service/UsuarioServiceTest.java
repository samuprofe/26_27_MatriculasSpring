package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.repository.DetalleUsuarioRepository;
import es.iesjuanbosco.matriculasspring.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    private UsuarioRepository usuarios;
    private DetalleUsuarioRepository detalles;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        usuarios = mock(UsuarioRepository.class);
        detalles = mock(DetalleUsuarioRepository.class);
        service = new UsuarioService(usuarios, detalles);
    }

    @Test
    void createsDetailOnlyForExistingUserAndLinksBothSides() {
        var usuario = Usuario.builder().id("u1").build();
        var detalle = DetalleUsuario.builder().id(99L).calle("Mayor").build();
        var saved = DetalleUsuario.builder().id(1L).build();
        when(usuarios.findById("u1")).thenReturn(Optional.of(usuario));
        when(detalles.save(detalle)).thenAnswer(invocation -> {
            assertNull(detalle.getId());
            assertSame(usuario, detalle.getUsuario());
            assertSame(detalle, usuario.getDetalleUsuario());
            return saved;
        });

        assertSame(saved, service.createDetalleUsuario("u1", detalle).orElseThrow());
        var missing = DetalleUsuario.builder().id(88L).build();
        assertTrue(service.createDetalleUsuario("missing", missing).isEmpty());
        assertEquals(88L, missing.getId());
        verify(usuarios).findById("u1");
        verify(usuarios).findById("missing");
        verify(detalles).save(detalle);
        verifyNoMoreInteractions(usuarios, detalles);
    }

    @Test
    void findsAndDeletesOnlyAnExistingDetail() {
        var detalle = DetalleUsuario.builder().id(1L).build();
        var usuario = Usuario.builder().id("u1").detalleUsuario(detalle).build();
        var sinDetalle = Usuario.builder().id("u2").build();
        when(usuarios.findById("u1")).thenReturn(Optional.of(usuario));
        when(usuarios.findById("u2")).thenReturn(Optional.of(sinDetalle));

        assertSame(detalle, service.findDetalleUsuario("u1").orElseThrow());
        assertTrue(service.findDetalleUsuario("u2").isEmpty());
        assertTrue(service.findDetalleUsuario("missing").isEmpty());
        doAnswer(invocation -> {
            assertNull(usuario.getDetalleUsuario());
            return null;
        }).when(detalles).delete(detalle);
        assertTrue(service.deleteDetalleUsuario("u1"));
        assertFalse(service.deleteDetalleUsuario("u2"));
        assertFalse(service.deleteDetalleUsuario("missing"));
        verify(usuarios, times(2)).findById("u1");
        verify(usuarios, times(2)).findById("u2");
        verify(usuarios, times(2)).findById("missing");
        verify(detalles).delete(detalle);
        verifyNoMoreInteractions(usuarios, detalles);
    }

    @Test
    void searchesAndMapsOnlyPublicListFields() {
        var usuario = Usuario.builder().id("u1").email("a@example.org").nombre("Ana")
                .apellidos("Pérez").password("secret").rol("ADMIN").build();
        var found = List.of(usuario);
        when(usuarios.findByNombreContainingIgnoreCase("an")).thenReturn(found);
        when(usuarios.findAll()).thenReturn(found);

        assertSame(found, service.buscarUsuariosPorNombre("an"));
        var result = service.getUsuarios();
        assertEquals(1, result.size());
        assertEquals("u1", result.get(0).getId());
        assertEquals("a@example.org", result.get(0).getEmail());
        assertEquals("Ana", result.get(0).getNombre());
        assertEquals("Pérez", result.get(0).getApellidos());
        verify(usuarios).findByNombreContainingIgnoreCase("an");
        verify(usuarios).findAll();
        verifyNoMoreInteractions(usuarios, detalles);
    }

    @Test
    void propagatesRepositoryFailures() {
        var failure = new IllegalStateException("failure");
        when(usuarios.findById("u1")).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> service.createDetalleUsuario("u1", new DetalleUsuario())));
        verify(usuarios).findById("u1");
        verifyNoMoreInteractions(usuarios, detalles);
    }
}
