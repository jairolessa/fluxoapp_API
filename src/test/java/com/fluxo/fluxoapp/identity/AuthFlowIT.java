package com.fluxo.fluxoapp.identity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Fluxo completo contra a aplicação real (H2 em memória, perfil test). Sem Docker. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthFlowIT {

    @LocalServerPort int port;
    final HttpClient http = HttpClient.newHttpClient();

    HttpResponse<String> send(String method, String path, String token, String json) throws Exception {
        var b = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json");
        if (token != null) b.header("Authorization", "Bearer " + token);
        b.method(method, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json));
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    static String reg(String email) {
        return """
            {"fullName":"Maria Silva","email":"%s","password":"senha1234","acceptedTerms":true}""".formatted(email);
    }

    @Test
    void registerLoginAndMe() throws Exception {
        String email = "Maria-" + UUID.randomUUID() + "@Exemplo.com";

        var r = send("POST", "/api/v1/auth/register", null, reg(email));
        assertEquals(201, r.statusCode());
        assertTrue(r.body().contains("\"accessToken\""));

        assertEquals(409, send("POST", "/api/v1/auth/register", null, reg(email.toUpperCase())).statusCode());

        var login = send("POST", "/api/v1/auth/login", null,
                "{\"email\":\"" + email.toLowerCase() + "\",\"password\":\"senha1234\"}");
        assertEquals(200, login.statusCode());
        Matcher m = Pattern.compile("\"accessToken\":\"([^\"]+)\"").matcher(login.body());
        assertTrue(m.find());
        String token = m.group(1);

        var me = send("GET", "/api/v1/users/me", token, null);
        assertEquals(200, me.statusCode());
        assertTrue(me.body().contains("\"preferredName\":null") || !me.body().contains("\"preferredName\":\""));

        var pn = send("PATCH", "/api/v1/users/me/preferred-name", token, "{\"preferredName\":\"Mari\"}");
        assertEquals(200, pn.statusCode());
        assertTrue(pn.body().contains("\"preferredName\":\"Mari\""));
    }

    @Test
    void wrongPasswordAndNoTokenAndValidation() throws Exception {
        assertEquals(401, send("POST", "/api/v1/auth/login", null,
                "{\"email\":\"nao@existe.com\",\"password\":\"qualquer123\"}").statusCode());
        assertEquals(401, send("GET", "/api/v1/users/me", null, null).statusCode());
        assertEquals(401, send("GET", "/api/v1/users/me", "token-invalido", null).statusCode());
        assertEquals(400, send("POST", "/api/v1/auth/register", null,
                "{\"fullName\":\"A\",\"email\":\"x\",\"password\":\"1\",\"acceptedTerms\":false}").statusCode());
    }
}
