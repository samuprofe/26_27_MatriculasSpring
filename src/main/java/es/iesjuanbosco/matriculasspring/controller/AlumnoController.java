package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlumnoController {

    //Inyección de dependencias.
    // Spring se encarga de crear el objeto y destruirlo cuando sea necesario (ciclo de vida del objeto).
    @Autowired
    private AlumnoRepository alumnoRepository;

    //Controlador para la URL http://localhost:8080/alumnos
    @GetMapping("/alumnos")
    public List<Alumno> findAll() {
        return alumnoRepository.findAll();
    }

    @GetMapping("/alumnos/{id}")
    public Alumno findById(@PathVariable Long id) {
        return alumnoRepository.findById(id).get();
    }
}
