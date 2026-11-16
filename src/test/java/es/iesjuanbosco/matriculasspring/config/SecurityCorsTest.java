package es.iesjuanbosco.matriculasspring.config;

import es.iesjuanbosco.matriculasspring.CorsConfig;
import es.iesjuanbosco.matriculasspring.controller.AlumnoController;
import es.iesjuanbosco.matriculasspring.service.AlumnoService;
import es.iesjuanbosco.matriculasspring.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlumnoController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtService.class})
class SecurityCorsTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private AlumnoService alumnoService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void permitePreflightDeLoginYDeRutasProtegidas() throws Exception {
        for (String origen : List.of("http://localhost:5500", "null")) {
            for (String ruta : List.of("/auth/login", "/auth/register", "/alumnos")) {
                mvc.perform(options(ruta)
                                .header(HttpHeaders.ORIGIN, origen)
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                        ruta.equals("/alumnos") ? "GET" : "POST")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                        .andExpect(status().isOk())
                        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origen));
            }
        }
    }

    @Test
    void exigeTokenYPermiteJwtValidoDesdeOtroOrigen() throws Exception {
        mvc.perform(get("/alumnos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/alumnos").header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());

        var authentication = new UsernamePasswordAuthenticationToken(
                "usuario@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtService.generateToken(authentication);
        mvc.perform(get("/alumnos")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5500")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5500"));
    }
}
