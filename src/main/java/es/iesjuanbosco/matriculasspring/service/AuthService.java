package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioRequest;
import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.exception.EmailRepetidoExpepcion;
import es.iesjuanbosco.matriculasspring.repository.UsuarioRepository;
import jakarta.validation.constraints.Size;
import jakarta.websocket.Encoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse register(UsuarioRequest usuarioRequest) {
        if (usuarioRepository.findByEmailIgnoreCase(usuarioRequest.getEmail()).isPresent()) {
            throw new EmailRepetidoExpepcion();
        }

        Usuario usuario = Usuario.builder()
                .apellidos(usuarioRequest.getApellidos())
                .email(usuarioRequest.getEmail())
                .fechaNacimiento(usuarioRequest.getFechaNacimiento())
                .nombre(usuarioRequest.getNombre())
                .password(passwordEncoder.encode(usuarioRequest.getPassword()))
                .roles("ROLE_USER") // Asignar el rol de usuario por defecto
                .activo(true)
                .build();
        usuarioRepository.saveAndFlush(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .build();
    }
}
