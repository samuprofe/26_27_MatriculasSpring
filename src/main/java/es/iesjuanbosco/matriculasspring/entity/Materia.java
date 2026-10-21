package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor
@AllArgsConstructor
@Entity
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String abreviatura;

    @JsonIgnore
    @ManyToMany(mappedBy = "materias")
    private List<Curso> cursos = new ArrayList<>();
}
