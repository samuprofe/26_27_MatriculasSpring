package es.iesjuanbosco.matriculasspring.repository;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    //public Alumno findByEmail(String email);
}
