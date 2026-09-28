package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/alumnos/{id}")
    public void deleteById(@PathVariable Long id)
    {
        alumnoRepository.deleteById(id);
    }

    @PutMapping("/alumnos/{id}")
    public Alumno update(@PathVariable Long id, @RequestBody Alumno alumno) {
        alumno.setId(id);
        return alumnoRepository.save(alumno);
    }

    @PostMapping("/alumnos")
    public Alumno create(@RequestBody Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

}
