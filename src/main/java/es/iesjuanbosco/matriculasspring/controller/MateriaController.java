package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.service.MateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    //GET http://localhost:8080/materias
    @GetMapping("/materias")
    public List<Materia> findAll() {
        return materiaService.findAll();
    }

    //GET http://localhost:8080/materias/5
    @GetMapping("/materias/{id}")
    public ResponseEntity<Materia> findById(@PathVariable Long id) {
        return materiaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //POST http://localhost:8080/materias
    @PostMapping("/materias")
    public ResponseEntity<Materia> create(@RequestBody Materia materia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaService.create(materia));
    }

    //PUT http://localhost:8080/materias/5
    @PutMapping("/materias/{id}")
    public ResponseEntity<Materia> update(@PathVariable Long id, @RequestBody Materia materia) {
        return materiaService.update(id, materia)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //DELETE http://localhost:8080/materias/5
    @DeleteMapping("/materias/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!materiaService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
