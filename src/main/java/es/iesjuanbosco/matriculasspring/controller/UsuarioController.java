package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/{usuarioId}/detalle-usuario")
    public ResponseEntity<DetalleUsuario> createDetalleUsuario(
            @PathVariable String usuarioId,
            @RequestBody DetalleUsuario detalleUsuario) {
        return usuarioService.createDetalleUsuario(usuarioId, detalleUsuario)
                .map(detalle -> ResponseEntity.status(HttpStatus.CREATED).body(detalle))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{usuarioId}/detalle-usuario")
    public ResponseEntity<DetalleUsuario> findDetalleUsuario(@PathVariable String usuarioId) {
        return usuarioService.findDetalleUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{usuarioId}/detalle-usuario")
    public ResponseEntity<?> deleteDetalleUsuario(@PathVariable String usuarioId) {
        return usuarioService.deleteDetalleUsuario(usuarioId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarUsuariosPorNombre(nombre));
    }

    @GetMapping
    public ResponseEntity<?> getUsuarios() {
        return ResponseEntity.ok(usuarioService.getUsuarios());
    }
}
