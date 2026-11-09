package es.iesjuanbosco.matriculasspring.service;

import es.iesjuanbosco.matriculasspring.DTO.MatriculaResumenResponse;
import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.entity.Matricula;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import es.iesjuanbosco.matriculasspring.repository.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatriculaService {
    private final MatriculaRepository matriculaRepository;
    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository, AlumnoRepository alumnoRepository,
                            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<Matricula> findAll() {
        return matriculaRepository.findAll();
    }

    public Optional<Matricula> create(Matricula matricula) {
        Optional<Alumno> alumno = alumnoRepository.findById(matricula.getAlumno().getId());
        Optional<Curso> curso = cursoRepository.findById(matricula.getCurso().getId());
        if (alumno.isEmpty() || curso.isEmpty()) {
            return Optional.empty();
        }
        Matricula nueva = Matricula.builder().alumno(alumno.get()).curso(curso.get()).build();
        return Optional.of(matriculaRepository.save(nueva));
    }

    public boolean deleteById(Long id) {
        if (!matriculaRepository.existsById(id)) {
            return false;
        }
        matriculaRepository.deleteById(id);
        return true;
    }

    public Optional<List<Matricula>> findByCurso(Long cursoId) {
        if (!cursoRepository.existsById(cursoId)) {
            return Optional.empty();
        }
        return Optional.of(matriculaRepository.findByCursoId(cursoId));
    }

    public Optional<List<Matricula>> findByAlumno(Long alumnoId) {
        if (!alumnoRepository.existsById(alumnoId)) {
            return Optional.empty();
        }
        return Optional.of(matriculaRepository.findByAlumnoId(alumnoId));
    }

    public List<MatriculaResumenResponse> getMatriculasResumen() {
        return matriculaRepository.findAll().stream()
                .map(matricula -> MatriculaResumenResponse.builder()
                        .nombreAlumno(matricula.getAlumno().getNombre())
                        .nombreCurso(matricula.getCurso().getNombre())
                        .idMatricula(matricula.getId())
                        .build())
                .toList();
    }
}
