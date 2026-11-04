package es.iesjuanbosco.matriculasspring.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   //Como no son entidades y no hay relaciones se puede usar @Data para generar los getters y setters
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioResponse {
    private String id;
    private String email;
    private String nombre;
    private String apellidos;
}
