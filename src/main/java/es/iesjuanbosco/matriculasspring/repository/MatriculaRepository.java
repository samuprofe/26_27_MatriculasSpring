package es.iesjuanbosco.matriculasspring.repository;

import es.iesjuanbosco.matriculasspring.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByCursoId(Long cursoId);

    List<Matricula> findByAlumnoId(Long alumnoId);

    Optional<Matricula> findByCursoLectivo(String cursoLectivo);
}
