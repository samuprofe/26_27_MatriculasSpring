package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.repository.DetalleUsuarioRepository;
import es.iesjuanbosco.matriculasspring.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final DetalleUsuarioRepository detalleUsuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, DetalleUsuarioRepository detalleUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.detalleUsuarioRepository = detalleUsuarioRepository;
    }

    public Optional<DetalleUsuario> createDetalleUsuario(String usuarioId, DetalleUsuario detalleUsuario) {
        return usuarioRepository.findById(usuarioId).map(usuario -> {
            detalleUsuario.setId(null);
            detalleUsuario.setUsuario(usuario);
            usuario.setDetalleUsuario(detalleUsuario);
            return detalleUsuarioRepository.save(detalleUsuario);
        });
    }

    public Optional<DetalleUsuario> findDetalleUsuario(String usuarioId) {
        return usuarioRepository.findById(usuarioId).map(Usuario::getDetalleUsuario);
    }

    public boolean deleteDetalleUsuario(String usuarioId) {
        return usuarioRepository.findById(usuarioId).map(usuario -> {
            DetalleUsuario detalleUsuario = usuario.getDetalleUsuario();
            if (detalleUsuario == null) {
                return false;
            }
            usuario.setDetalleUsuario(null);
            detalleUsuarioRepository.delete(detalleUsuario);
            return true;
        }).orElse(false);
    }

    public List<Usuario> buscarUsuariosPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<UsuarioResponse> getUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> UsuarioResponse.builder()
                        .email(usuario.getEmail())
                        .nombre(usuario.getNombre())
                        .apellidos(usuario.getApellidos())
                        .id(usuario.getId())
                        .build())
                .toList();
    }
}
