package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioRequest;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.exception.EmailRepetidoExpepcion;
import es.iesjuanbosco.matriculasspring.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    private UsuarioRepository repository;
    private AuthService service;
    private UsuarioRequest request;

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioRepository.class);
        service = new AuthService(repository);
        request = UsuarioRequest.builder()
                .email("Persona@example.com").nombre("Persona").apellidos("Prueba")
                .password("password123").fechaNacimiento(LocalDate.of(2000, 1, 2)).build();
    }

    @Test
    void registersAllFieldsAndBuildsResponseFromPersistedEntity() {
        when(repository.saveAndFlush(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            assertEquals(request.getEmail(), usuario.getEmail());
            assertEquals(request.getNombre(), usuario.getNombre());
            assertEquals(request.getApellidos(), usuario.getApellidos());
            assertEquals(request.getPassword(), usuario.getPassword());
            assertEquals(request.getFechaNacimiento(), usuario.getFechaNacimiento());
            usuario.setId("id-1");
            return usuario;
        });

        var response = service.register(request);

        assertEquals("id-1", response.getId());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getApellidos(), response.getApellidos());
        var order = inOrder(repository);
        order.verify(repository).findByEmailIgnoreCase(request.getEmail());
        order.verify(repository).saveAndFlush(any(Usuario.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void duplicateEmailIsRejectedWithoutSaving() {
        when(repository.findByEmailIgnoreCase(request.getEmail()))
                .thenReturn(Optional.of(Usuario.builder().email("persona@example.com").build()));

        assertThrows(EmailRepetidoExpepcion.class, () -> service.register(request));

        verify(repository).findByEmailIgnoreCase(request.getEmail());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void lookupFailurePropagatesWithoutSaving() {
        var failure = new IllegalStateException("lookup failed");
        when(repository.findByEmailIgnoreCase(request.getEmail())).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.register(request)));
        verify(repository).findByEmailIgnoreCase(request.getEmail());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void integrityFailurePropagatesWithoutRecheckingEmail() {
        var failure = new DataIntegrityViolationException("integrity violation");
        when(repository.saveAndFlush(any(Usuario.class))).thenThrow(failure);

        assertSame(failure, assertThrows(DataIntegrityViolationException.class, () -> service.register(request)));
        verify(repository).findByEmailIgnoreCase(request.getEmail());
        verify(repository).saveAndFlush(any(Usuario.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void otherSaveFailurePropagatesWithoutRecheckingEmail() {
        var failure = new IllegalStateException("save failed");
        when(repository.saveAndFlush(any(Usuario.class))).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.register(request)));
        verify(repository).findByEmailIgnoreCase(request.getEmail());
        verify(repository).saveAndFlush(any(Usuario.class));
        verifyNoMoreInteractions(repository);
    }
}
