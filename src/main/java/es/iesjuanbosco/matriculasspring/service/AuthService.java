package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioRequest;
import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.exception.EmailRepetidoExpepcion;
import es.iesjuanbosco.matriculasspring.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
                .password(usuarioRequest.getPassword())
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
