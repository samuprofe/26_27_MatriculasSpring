package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String abreviatura;

    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.REMOVE)
    private List<Matricula> matriculas;

    public enum Nivel {
        ESO,
        BACHILLERATO,
        CFGS,
        CFGM,
        CFGB
    }
}
