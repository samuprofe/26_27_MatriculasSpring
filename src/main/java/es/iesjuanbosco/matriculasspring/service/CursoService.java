package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import es.iesjuanbosco.matriculasspring.repository.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;

    public CursoService(CursoRepository cursoRepository, MateriaRepository materiaRepository) {
        this.cursoRepository = cursoRepository;
        this.materiaRepository = materiaRepository;
    }

    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> findById(Long id) {
        return cursoRepository.findById(id);
    }

    public Curso create(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Optional<Curso> update(Long id, Curso curso) {
        if (!cursoRepository.existsById(id)) {
            return Optional.empty();
        }
        curso.setId(id);
        return Optional.of(cursoRepository.save(curso));
    }

    public boolean deleteById(Long id) {
        if (!cursoRepository.existsById(id)) {
            return false;
        }
        cursoRepository.deleteById(id);
        return true;
    }

    public void deleteAll() {
        cursoRepository.deleteAll();
    }

    @Transactional
    public Optional<Curso> addMateria(Long cursoId, Long materiaId) {
        Optional<Curso> curso = cursoRepository.findById(cursoId);
        Optional<Materia> materia = materiaRepository.findById(materiaId);
        if (curso.isEmpty() || materia.isEmpty()) {
            return Optional.empty();
        }
        curso.get().addMateria(materia.get());
        return Optional.of(cursoRepository.save(curso.get()));
    }

    @Transactional
    public boolean removeMateria(Long cursoId, Long materiaId) {
        Optional<Curso> curso = cursoRepository.findById(cursoId);
        Optional<Materia> materia = materiaRepository.findById(materiaId);
        if (curso.isEmpty() || materia.isEmpty() || !curso.get().removeMateria(materia.get())) {
            return false;
        }
        cursoRepository.save(curso.get());
        return true;
    }
}
