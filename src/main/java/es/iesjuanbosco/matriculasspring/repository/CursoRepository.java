package es.iesjuanbosco.matriculasspring.repository;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
