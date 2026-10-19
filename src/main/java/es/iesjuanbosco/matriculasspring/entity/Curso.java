package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @ManyToMany
    private List<Materia> materias;

    public void addMateria(Materia materia) {
        if (materias == null) {
            materias = new ArrayList<>();
        }
        if (!materias.contains(materia)) {
            materias.add(materia);
            if (materia.getCursos() == null) {
                materia.setCursos(new ArrayList<>());
            }
            materia.getCursos().add(this);
        }
    }

    public boolean removeMateria(Materia materia) {
        if (materias == null) {
            return false;
        }
        if(!materias.remove(materia)) {
            return false;
        }
        if (materia.getCursos() != null) {
            materia.getCursos().remove(this);
        }
        return true;
    }

    public enum Nivel {
        ESO,
        BACHILLERATO,
        CFGS,
        CFGM,
        CFGB
    }
}
