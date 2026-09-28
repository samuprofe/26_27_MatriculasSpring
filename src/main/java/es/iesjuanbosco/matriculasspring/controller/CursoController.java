package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CursoController {

    //Inyección de dependencias.
    //Spring se encarga de crear el objeto y destruirlo cuando sea necesario (ciclo de vida del objeto).
    @Autowired
    private CursoRepository cursoRepository;

    //GET http://localhost:8080/cursos --> Obtiene todos los cursos
    @GetMapping("/cursos")
    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    //GET http://localhost:8080/cursos/{id} --> Obtiene un curso
    @GetMapping("/cursos/{id}")
    public Curso findById(@PathVariable Long id) {
        return cursoRepository.findById(id).get();
    }

    //DELETE http://localhost:8080/cursos/{id} --> Borra un curso
    @DeleteMapping("/cursos/{id}")
    public void deleteById(@PathVariable Long id) {
        cursoRepository.deleteById(id);
    }

    //PUT http://localhost:8080/cursos/{id} --> Modifica un curso
    @PutMapping("/cursos/{id}")
    public Curso update(@PathVariable Long id, @RequestBody Curso curso) {
        curso.setId(id);
        return cursoRepository.save(curso);
    }

    //POST http://localhost:8080/cursos --> Inserta un curso
    //El curso lo recibe en el cuerpo de la petición en formato JSON
    //y lo recogemos en con la anotación @RequestBody
    @PostMapping("/cursos")
    public Curso create(@RequestBody Curso curso) {
        return cursoRepository.save(curso);
    }

    //DELETE http://localhost:8080/cursos
    @DeleteMapping("/cursos")
    public void deleteAll() {
        cursoRepository.deleteAll();
    }

}

