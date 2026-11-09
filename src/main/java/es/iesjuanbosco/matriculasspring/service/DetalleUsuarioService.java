package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.repository.DetalleUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleUsuarioService {
    private final DetalleUsuarioRepository detalleUsuarioRepository;

    public DetalleUsuarioService(DetalleUsuarioRepository detalleUsuarioRepository) {
        this.detalleUsuarioRepository = detalleUsuarioRepository;
    }

    public List<DetalleUsuario> findAll() {
        return detalleUsuarioRepository.findAll();
    }

    public Optional<DetalleUsuario> findById(Long id) {
        return detalleUsuarioRepository.findById(id);
    }

    public DetalleUsuario create(DetalleUsuario detalleUsuario) {
        detalleUsuario.setId(null);
        return detalleUsuarioRepository.save(detalleUsuario);
    }

    public Optional<DetalleUsuario> update(Long id, DetalleUsuario detalleUsuario) {
        if (!detalleUsuarioRepository.existsById(id)) {
            return Optional.empty();
        }
        detalleUsuario.setId(id);
        return Optional.of(detalleUsuarioRepository.save(detalleUsuario));
    }

    public boolean deleteById(Long id) {
        if (!detalleUsuarioRepository.existsById(id)) {
            return false;
        }
        detalleUsuarioRepository.deleteById(id);
        return true;
    }
}
