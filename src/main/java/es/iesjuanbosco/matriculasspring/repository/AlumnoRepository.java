package es.iesjuanbosco.matriculasspring.repository;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumnoRepository extends CrudRepository<Alumno, Long> {

}
