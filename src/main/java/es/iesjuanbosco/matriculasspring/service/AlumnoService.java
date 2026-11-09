package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {
    private final AlumnoRepository alumnoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    public List<Alumno> findAll() {
        return alumnoRepository.findAll();
    }

    public Optional<Alumno> findById(Long id) {
        return alumnoRepository.findById(id);
    }

    public void deleteById(Long id) {
        alumnoRepository.deleteById(id);
    }

    public Optional<Alumno> update(Long id, Alumno alumno) {
        if (!alumnoRepository.existsById(id)) {
            return Optional.empty();
        }
        alumno.setId(id);
        return Optional.of(alumnoRepository.save(alumno));
    }

    public Alumno create(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    public Optional<Alumno> modifyImporteBeca(Long id, BigDecimal importeBeca) {
        return alumnoRepository.findById(id).map(alumno -> {
            alumno.setImporteBeca(importeBeca);
            return alumnoRepository.save(alumno);
        });
    }
}
