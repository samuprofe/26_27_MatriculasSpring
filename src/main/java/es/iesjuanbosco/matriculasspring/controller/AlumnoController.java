package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.Alumno;
import es.iesjuanbosco.matriculasspring.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class AlumnoController {

    //Inyección de dependencias.
    // Spring se encarga de crear el objeto y destruirlo cuando sea necesario (ciclo de vida del objeto).
    @Autowired
    private AlumnoRepository alumnoRepository;

    //GET http://localhost:8080/alumnos
    @GetMapping("/alumnos")
    public List<Alumno> findAll() {
        return alumnoRepository.findAll();
    }

    //GET http://localhost:8080/alumnos/5
    @GetMapping("/alumnos/{id}")
    public ResponseEntity<Alumno> findById(@PathVariable Long id) {
/*      Optional<Alumno> alumnoOptional=  alumnoRepository.findById(id);
        if (alumnoOptional.isPresent()){
            //Devolvemos el alumno con el código 200 OK
            return ResponseEntity.ok(alumnoOptional.get());
        }
        else{
            //Devolvemos un código 404 Not Found
            return ResponseEntity.notFound().build();
        }*/

        /*return alumnoRepository.findById(id)
                .map(alumno ->                      //Si el alumno existe en la BD
                        ResponseEntity.ok(alumno))
                .orElseGet(() ->
                    ResponseEntity.notFound().build());*/ //Si el alumno no existe en la BD

        return alumnoRepository.findById(id)
                .map(ResponseEntity::ok)    //Si el alumno existe en la BD
                .orElseGet(() -> ResponseEntity.notFound().build()); //Si el alumno no existe en la BD
    }

    //DELETE http://localhost:8080/alumnos/5
    @DeleteMapping("/alumnos/{id}")
    public void deleteById(@PathVariable Long id)
    {
        alumnoRepository.deleteById(id);
    }

    //PUT http://localhost:8080/alumnos/5
    @PutMapping("/alumnos/{id}")
    public Alumno update(@PathVariable Long id, @RequestBody Alumno alumno) {
        alumno.setId(id);
        return alumnoRepository.save(alumno);
    }

    //POST http://localhost:8080/alumnos
    @PostMapping("/alumnos")
    public Alumno create(@RequestBody Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    //PATCH http://localhost:8080/alumnos/5/importe-beca
    //PATCH se utiliza par modificar campos concretos de un objeto
    @PatchMapping("/alumnos/{id}/importe-beca")
    public Alumno modifyImporteBeca(@PathVariable Long id, @RequestBody BigDecimal importeBeca) {
/*        Optional<Alumno> alumnoOptional = alumnoRepository.findById(id);
        if(alumnoOptional.isPresent())
        {
            Alumno alumno = alumnoOptional.get();
            alumno.setImporteBeca(importeBeca); //Modifico el importe de la beca en el objeto
            return alumnoRepository.save(alumno);   //Actualizo el alumno en la BD
        }
        else{
            return null;
        }
    }*/
        return alumnoRepository.findById(id)
                .map(alumno -> {    //Si el alumno existe en la BD ejecuta .map
                    alumno.setImporteBeca(importeBeca);
                    return alumnoRepository.save(alumno);
                })
                .orElseGet(() -> {      //Si el alumno no existe en la BD ejecuta .orElseGet
                    return null;
                });
    }
}
