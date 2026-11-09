package es.iesjuanbosco.matriculasspring.exception;

public class EmailRepetidoExpepcion extends RuntimeException {
    public EmailRepetidoExpepcion() {
        super("El email ya está registrado");
    }

    public EmailRepetidoExpepcion(Throwable cause) {
        super("El email ya está registrado", cause);
    }
}
