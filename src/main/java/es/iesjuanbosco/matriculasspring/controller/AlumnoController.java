package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping("/alumnos")
    public List<Alumno> findAll() {
        return alumnoService.findAll();
    }

    @GetMapping("/alumnos/{id}")
    public ResponseEntity<Alumno> findById(@PathVariable Long id) {
        return alumnoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/alumnos/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        alumnoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/alumnos/{id}")
    public ResponseEntity<Alumno> update(@PathVariable Long id, @RequestBody Alumno alumno) {
        return alumnoService.update(id, alumno)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/alumnos")
    public ResponseEntity<Alumno> create(@RequestBody Alumno alumno) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alumnoService.create(alumno));
    }

    @PatchMapping("/alumnos/{id}/importe-beca")
    public ResponseEntity<Alumno> modifyImporteBeca(@PathVariable Long id, @RequestBody BigDecimal importeBeca) {
        return alumnoService.modifyImporteBeca(id, importeBeca)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
