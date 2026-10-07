package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @Column(length = 100)
    private String email;

    private LocalDate fechaNacimiento;

    @Column(length = 10)
    private String DNI;

    @Column(length = 15 )
    private String telefono;

    @Column(precision = 7, scale = 3) //dentre 0 y 999.999,99
    private BigDecimal ImporteBeca;

    @JsonIgnore
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.REMOVE)
    private List<Matricula> matriculas;
}
