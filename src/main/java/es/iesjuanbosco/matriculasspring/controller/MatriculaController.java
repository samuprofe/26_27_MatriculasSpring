package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Matricula;
import es.iesjuanbosco.matriculasspring.service.MatriculaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping("/matriculas")
    public List<Matricula> findAll() {
        return matriculaService.findAll();
    }

    @PostMapping("/matriculas")
    public ResponseEntity<Matricula> create(@RequestBody Matricula matricula) {
        if (matricula.getAlumno() == null || matricula.getAlumno().getId() == null
                || matricula.getCurso() == null || matricula.getCurso().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        return matriculaService.create(matricula)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/matriculas/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!matriculaService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cursos/{cursoId}/matriculas")
    public ResponseEntity<List<Matricula>> findByCurso(@PathVariable Long cursoId) {
        return matriculaService.findByCurso(cursoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/alumnos/{alumnoId}/matriculas")
    public ResponseEntity<List<Matricula>> findByAlumno(@PathVariable Long alumnoId) {
        return matriculaService.findByAlumno(alumnoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/matriculas/resumen")
    public ResponseEntity<?> getMatriculasResumen() {
        return ResponseEntity.ok(matriculaService.getMatriculasResumen());
    }
}
