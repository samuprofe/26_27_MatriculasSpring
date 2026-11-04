package es.iesjuanbosco.matriculasspring.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioRequest {
    private String apellidos;

    @Email (message = "El campo email debe ser un email válido")
    @NotEmpty (message = "El campo email no puede estar vacío")
    private String email;

    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;

    @NotEmpty(message = "El campo nombre no puede estar vacío")
    private String nombre;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
