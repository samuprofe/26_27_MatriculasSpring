package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Materia;
import es.iesjuanbosco.matriculasspring.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MateriaController {

    @Autowired
    private MateriaRepository materiaRepository;

    //GET http://localhost:8080/materias
    @GetMapping("/materias")
    public List<Materia> findAll() {
        return materiaRepository.findAll();
    }

    //GET http://localhost:8080/materias/5
    @GetMapping("/materias/{id}")
    public ResponseEntity<Materia> findById(@PathVariable Long id) {
        return materiaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //POST http://localhost:8080/materias
    @PostMapping("/materias")
    public ResponseEntity<Materia> create(@RequestBody Materia materia) {
        materia.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaRepository.save(materia));
    }

    //PUT http://localhost:8080/materias/5
    @PutMapping("/materias/{id}")
    public ResponseEntity<Materia> update(@PathVariable Long id, @RequestBody Materia materia) {
        if (!materiaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        materia.setId(id);
        return ResponseEntity.ok(materiaRepository.save(materia));
    }

    //DELETE http://localhost:8080/materias/5
    @DeleteMapping("/materias/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!materiaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        materiaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
