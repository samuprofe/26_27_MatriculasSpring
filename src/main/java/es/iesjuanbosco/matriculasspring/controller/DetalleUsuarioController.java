package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.repository.DetalleUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-usuarios")
public class DetalleUsuarioController {

    @Autowired
    private DetalleUsuarioRepository detalleUsuarioRepository;

    @GetMapping
    public List<DetalleUsuario> findAll() {
        return detalleUsuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleUsuario> findById(@PathVariable Long id) {
        return detalleUsuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleUsuario> create(@RequestBody DetalleUsuario detalleUsuario) {
        detalleUsuario.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detalleUsuarioRepository.save(detalleUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleUsuario> update(
            @PathVariable Long id,
            @RequestBody DetalleUsuario detalleUsuario) {
        if (!detalleUsuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        detalleUsuario.setId(id);
        return ResponseEntity.ok(detalleUsuarioRepository.save(detalleUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!detalleUsuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        detalleUsuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
