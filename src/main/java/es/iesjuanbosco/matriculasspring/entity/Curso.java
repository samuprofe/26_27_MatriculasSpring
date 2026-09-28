package es.iesjuanbosco.matriculasspring.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
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

    public enum Nivel {
        ESO,
        BACHILLERATO,
        CFGS,
        CFGM,
        CFGB
    }
}
