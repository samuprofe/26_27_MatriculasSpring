package es.iesjuanbosco.matriculasspring.controller;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioResponse;
import es.iesjuanbosco.matriculasspring.entity.DetalleUsuario;
import es.iesjuanbosco.matriculasspring.entity.Usuario;
import es.iesjuanbosco.matriculasspring.repository.DetalleUsuarioRepository;
import es.iesjuanbosco.matriculasspring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DetalleUsuarioRepository detalleUsuarioRepository;

    @PostMapping("/{usuarioId}/detalle-usuario")
    public ResponseEntity<DetalleUsuario> createDetalleUsuario(
            @PathVariable String usuarioId,
            @RequestBody DetalleUsuario detalleUsuario) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> {
                    detalleUsuario.setId(null);
                    detalleUsuario.setUsuario(usuario);
                    usuario.setDetalleUsuario(detalleUsuario);
                    DetalleUsuario detalleGuardado = detalleUsuarioRepository.save(detalleUsuario);
                    return ResponseEntity.status(HttpStatus.CREATED).body(detalleGuardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{usuarioId}/detalle-usuario")
    public ResponseEntity<DetalleUsuario> findDetalleUsuario(@PathVariable String usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getDetalleUsuario)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{usuarioId}/detalle-usuario")
    public ResponseEntity<?> deleteDetalleUsuario(@PathVariable String usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> {
                    DetalleUsuario detalleUsuario = usuario.getDetalleUsuario();
                    if (detalleUsuario == null) {
                        return ResponseEntity.notFound().build();
                    }
                    usuario.setDetalleUsuario(null);
                    detalleUsuarioRepository.delete(detalleUsuario);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Devuelve todos los usuarios que contengan un texto en el nombre sin importar mayusculas
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioRepository.findByNombreContainingIgnoreCase(nombre));

    }

    @GetMapping
    public ResponseEntity<?> getUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        //Convierto la lista de usuarios a una lista de DTOs para no devolver la contraseña
        List<UsuarioResponse> usuariosResponseList = new ArrayList<>();
        usuarios.forEach(usuario -> {
            usuariosResponseList.add(
                    UsuarioResponse.builder()
                    .email(usuario.getEmail())
                    .nombre(usuario.getNombre())
                    .apellidos(usuario.getApellidos())
                    .id(usuario.getId())
                    .build());
        });

        return ResponseEntity.ok(usuariosResponseList);

        /*
        List<UsuarioResponse> usuariosResponse = usuarios.stream()
                .map(usuario -> {
                    UsuarioResponse usuarioResponse = new Usuario();
                    usuarioResponse.setNombre(usuario.getNombre());
                    usuarioResponse.setEmail(usuario.getEmail());
                    usuarioResponse.setPassword(null); // No devolver la contraseña
                    return usuarioResponse;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosResponse);
        */
    }
}
