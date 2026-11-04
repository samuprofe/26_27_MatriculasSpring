package es.iesjuanbosco.matriculasspring.repository;

import es.iesjuanbosco.matriculasspring.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findByNombre(String nombre);
    //Encontrar todos los usuarios que el nombre contenta un texto sin importar mayusculas
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findTop1ByNombreOrderByNombreAsc(String nombre);

}
