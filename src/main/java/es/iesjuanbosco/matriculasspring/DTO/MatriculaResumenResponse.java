package es.iesjuanbosco.matriculasspring.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatriculaResumenResponse {
    private String nombreAlumno;
    private String nombreCurso;
    private Long idMatricula;
}
