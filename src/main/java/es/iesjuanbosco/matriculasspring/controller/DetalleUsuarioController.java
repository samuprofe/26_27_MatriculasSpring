package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.service.DetalleUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-usuarios")
public class DetalleUsuarioController {

    private final DetalleUsuarioService detalleUsuarioService;

    public DetalleUsuarioController(DetalleUsuarioService detalleUsuarioService) {
        this.detalleUsuarioService = detalleUsuarioService;
    }

    @GetMapping
    public List<DetalleUsuario> findAll() {
        return detalleUsuarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleUsuario> findById(@PathVariable Long id) {
        return detalleUsuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleUsuario> create(@RequestBody DetalleUsuario detalleUsuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detalleUsuarioService.create(detalleUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleUsuario> update(
            @PathVariable Long id,
            @RequestBody DetalleUsuario detalleUsuario) {
        return detalleUsuarioService.update(id, detalleUsuario)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return detalleUsuarioService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
