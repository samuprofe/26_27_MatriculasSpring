package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.repository.MateriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService {
    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public List<Materia> findAll() {
        return materiaRepository.findAll();
    }

    public Optional<Materia> findById(Long id) {
        return materiaRepository.findById(id);
    }

    public Materia create(Materia materia) {
        materia.setId(null);
        return materiaRepository.save(materia);
    }

    public Optional<Materia> update(Long id, Materia materia) {
        if (!materiaRepository.existsById(id)) {
            return Optional.empty();
        }
        materia.setId(id);
        return Optional.of(materiaRepository.save(materia));
    }

    public boolean deleteById(Long id) {
        if (!materiaRepository.existsById(id)) {
            return false;
        }
        materiaRepository.deleteById(id);
        return true;
    }
}
