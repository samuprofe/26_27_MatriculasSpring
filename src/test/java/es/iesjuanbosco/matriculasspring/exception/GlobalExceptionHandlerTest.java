package es.iesjuanbosco.matriculasspring.exception;

import es.iesjuanbosco.matriculasspring.DTO.UsuarioRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void returnsConflictForRepeatedEmail() {
        var response = new GlobalExceptionHandler().handleEmailRepetido(new EmailRepetidoExpepcion());

        assertEquals(409, response.getStatusCode().value());
        assertEquals(Map.of("error", "El email ya está registrado"), response.getBody());
    }

    @Test
    void returnsExceptionMessageWithoutDuplicatingIt() {
        var exception = new EmailRepetidoExpepcion() {
            @Override
            public String getMessage() {
                return "Otro mensaje";
            }
        };

        var response = new GlobalExceptionHandler().handleEmailRepetido(exception);

        assertEquals(Map.of("error", exception.getMessage()), response.getBody());
    }

    @Test
    void returnsBadRequestWithFieldErrors() throws NoSuchMethodException {
        UsuarioRequest request = UsuarioRequest.builder().build();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "usuarioRequest");
        bindingResult.addError(new FieldError("usuarioRequest", "email", "Debe ser válido"));
        bindingResult.addError(new FieldError("usuarioRequest", "nombre", "No puede estar vacío"));
        Method method = TestController.class.getDeclaredMethod("register", UsuarioRequest.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                new MethodParameter(method, 0), bindingResult);

        var response = new GlobalExceptionHandler().handleMethodArgumentNotValid(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(Map.of(
                "email", "Debe ser válido",
                "nombre", "No puede estar vacío"
        ), response.getBody());
    }

    private static class TestController {
        void register(UsuarioRequest usuarioRequest) {
        }
    }
}
