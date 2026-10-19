package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/*
- Registrar qué usuario añade cada matricula
- Añadir repositorio y controlador para Usuario (sólo registrar, sin seguridad)

 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;
    private String nombre;
    private String apellidos;
    private String password;
    private String rol;
    private LocalDate fechaNacimiento;

    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaUltimaModificacion;

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
}
