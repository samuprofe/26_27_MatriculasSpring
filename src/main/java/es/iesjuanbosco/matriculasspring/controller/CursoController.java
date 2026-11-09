package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    //GET http://localhost:8080/cursos --> Obtiene todos los cursos
    @GetMapping("/cursos")  //EndPoint
    public List<Curso> findAll() {
        return cursoService.findAll();
    }

    //GET http://localhost:8080/cursos/{id} --> Obtiene un curso
    @GetMapping("/cursos/{id}")
    public ResponseEntity<Curso> findById(@PathVariable Long id) {
        return cursoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //DELETE http://localhost:8080/cursos/{id} --> Borra un curso
    @DeleteMapping("/cursos/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!cursoService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    //PUT http://localhost:8080/cursos/{id} --> Modifica un curso
    @PutMapping("/cursos/{id}")
    public ResponseEntity<Curso> update(@PathVariable Long id, @RequestBody Curso curso) {
        return cursoService.update(id, curso)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //POST http://localhost:8080/cursos --> Inserta un curso
    //El curso lo recibe en el cuerpo de la petición en formato JSON
    //y lo recogemos en con la anotación @RequestBody
    @PostMapping("/cursos")
    public ResponseEntity<Curso> create(@RequestBody Curso curso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.create(curso));
    }

    //DELETE http://localhost:8080/cursos
    @DeleteMapping("/cursos")
    public ResponseEntity<Void> deleteAll() {
        cursoService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    //POST http://localhost:8080/cursos/{cursoId}/materias/{materiaId} --> Añade una materia al curso
    @PostMapping("/cursos/{cursoId}/materias/{materiaId}")
    public ResponseEntity<Curso> addMateria(@PathVariable Long cursoId, @PathVariable Long materiaId) {
        return cursoService.addMateria(cursoId, materiaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //DELETE http://localhost:8080/cursos/{cursoId}/materias/{materiaId} --> Quita una materia del curso
    @DeleteMapping("/cursos/{cursoId}/materias/{materiaId}")
    public ResponseEntity<Void> removeMateria(@PathVariable Long cursoId, @PathVariable Long materiaId) {
        if (!cursoService.removeMateria(cursoId, materiaId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
