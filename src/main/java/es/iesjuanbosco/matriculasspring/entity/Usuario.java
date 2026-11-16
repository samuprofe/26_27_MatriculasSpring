package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
- Registrar qué usuario añade cada matricula
- Añadir repositorio y controlador para Usuario (sólo registrar, sin seguridad)
 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email;
    private String nombre;
    private String apellidos;
    private String password;
    private String roles;
    private LocalDate fechaNacimiento;
    private Boolean activo;

    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaUltimaModificacion;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private DetalleUsuario detalleUsuario;

    @JsonIgnore //Al convertir a JSON, no incluir la lista de matriculas para evitar bucles infinitos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE)
    private List<Matricula> matriculas;

    // Se ejecuta automáticamente antes de insertar un nuevo usuario
    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    // Se ejecuta automáticamente antes de actualizar un usuario existente
    @PreUpdate
    protected void onUpdate() {

        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte los roles de String a GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles.split(",")) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }

        return authorities;

    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
