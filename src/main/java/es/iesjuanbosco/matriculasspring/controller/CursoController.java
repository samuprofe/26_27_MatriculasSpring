package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CursoController {

    //Inyección de dependencias.
    //Spring se encarga de crear el objeto y destruirlo cuando sea necesario (ciclo de vida del objeto).
    @Autowired
    private CursoRepository cursoRepository;

    //GET http://localhost:8080/cursos --> Obtiene todos los cursos
    @GetMapping("/cursos")  //EndPoint
    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    //GET http://localhost:8080/cursos/{id} --> Obtiene un curso
    @GetMapping("/cursos/{id}")
    public ResponseEntity<Curso> findById(@PathVariable Long id) {
        return cursoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //DELETE http://localhost:8080/cursos/{id} --> Borra un curso
    @DeleteMapping("/cursos/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!cursoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cursoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //PUT http://localhost:8080/cursos/{id} --> Modifica un curso
    @PutMapping("/cursos/{id}")
    public ResponseEntity<Curso> update(@PathVariable Long id, @RequestBody Curso curso) {
        if (!cursoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        curso.setId(id);
        return ResponseEntity.ok(cursoRepository.save(curso));
    }

    //POST http://localhost:8080/cursos --> Inserta un curso
    //El curso lo recibe en el cuerpo de la petición en formato JSON
    //y lo recogemos en con la anotación @RequestBody
    @PostMapping("/cursos")
    public ResponseEntity<Curso> create(@RequestBody Curso curso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoRepository.save(curso));
    }

    //DELETE http://localhost:8080/cursos
    @DeleteMapping("/cursos")
    public ResponseEntity<Void> deleteAll() {
        cursoRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

}
