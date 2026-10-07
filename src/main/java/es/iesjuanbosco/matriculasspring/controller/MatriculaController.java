package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.entity.Curso;
import es.iesjuanbosco.matriculasspring.entity.Matricula;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import es.iesjuanbosco.matriculasspring.repository.CursoRepository;
import es.iesjuanbosco.matriculasspring.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
public class MatriculaController {

    @Autowired
    private MatriculaRepository matriculaRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private CursoRepository cursoRepository;


    //GET http://localhost:8080/matriculas --> Obtiene todas las matrículas
    @GetMapping("/matriculas")
    public List<Matricula> findAll() {
        return matriculaRepository.findAll();   // 200 OK
    }

    //POST http://localhost:8080/matriculas --> Crea una matrícula
    //El alumno y el curso se indican por su id dentro del cuerpo: {"alumno":{"id":1},"curso":{"id":2},...}
    @PostMapping("/matriculas")
    public ResponseEntity<Matricula> create(@RequestBody Matricula matricula) {
        
        //Comprueba el formato del json recibido, si no es correcto devuelve un 400 Bad Request
        if (matricula.getAlumno() == null || matricula.getAlumno().getId() == null
                || matricula.getCurso() == null || matricula.getCurso().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Alumno> alumnoOptional = alumnoRepository.findById(matricula.getAlumno().getId());
        Optional<Curso> cursoOptional = cursoRepository.findById(matricula.getCurso().getId());
        if (alumnoOptional.isEmpty() || cursoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        //Si llega hasta aquí es que el formato del cuerpo (JSON) es correcto y el alumno y el curso existen
        Matricula matriculaNueva = new Matricula();
        matriculaNueva.setAlumno(alumnoOptional.get());
        matriculaNueva.setCurso(cursoOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaRepository.save(matriculaNueva));
    }

    //DELETE http://localhost:8080/matriculas/{id} --> Borra una matrícula
    @DeleteMapping("/matriculas/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!matriculaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        matriculaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //GET http://localhost:8080/cursos/{cursoId}/matriculas --> Matrículas de un curso
    @GetMapping("/cursos/{cursoId}/matriculas")
    public ResponseEntity<List<Matricula>> findByCurso(@PathVariable Long cursoId) {
        if (!cursoRepository.existsById(cursoId)) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(matriculaRepository.findByCursoId(cursoId));
    }

    //GET http://localhost:8080/alumnos/{alumnoId}/matriculas --> Matrículas de un alumno
    @GetMapping("/alumnos/{alumnoId}/matriculas")
    public ResponseEntity<List<Matricula>> findByAlumno(@PathVariable Long alumnoId) {
        if (!alumnoRepository.existsById(alumnoId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(matriculaRepository.findByAlumnoId(alumnoId));
    }
}
