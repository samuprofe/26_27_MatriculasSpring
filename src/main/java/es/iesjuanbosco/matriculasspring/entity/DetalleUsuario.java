package es.iesjuanbosco.matriculasspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor
@AllArgsConstructor
@Entity
public class DetalleUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String calle;
    private String numero;
    private String poblacion;
    private String provincia;
    private String telefono;
    private String codigoPostal;
    private String Comentarios;

    @JsonIgnore
    @OneToOne
    private Usuario usuario;
}
