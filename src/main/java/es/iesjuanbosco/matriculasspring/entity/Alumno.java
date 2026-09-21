package es.iesjuanbosco.matriculasspring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100,nullable = false )
    private String nombre;

    @Column(length = 200,nullable = false )
    private String apellidos;

    @Column(length = 100,nullable = false )
    private String email;

    private LocalDate fechaNacimiento;
}
