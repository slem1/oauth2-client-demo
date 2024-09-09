package fr.sle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@RestController
@RequestMapping("/")
public class Oauth2ClientResource {

    private final URI githubAuthorizeURL;
    private final URI githubAccessTokenURL;

    private final WebClient webclient;
    private final ObjectMapper objectMapper;

    public Oauth2ClientResource(WebClient webclient, ObjectMapper objectMapper) throws URISyntaxException {
        this.webclient = webclient;
        this.objectMapper = objectMapper;
        githubAuthorizeURL = new URI("https", "github.com", "/login/oauth/authorize", null);
        githubAccessTokenURL = new URI("https", "github.com", "/login/oauth/access_token", null);
    }

    @GetMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String state = "ABC123456";
        URI uri = UriComponentsBuilder.fromUri(githubAuthorizeURL)
                .queryParam("client_id", "Ov23li7gAKPzcEAVVsgs")
                .queryParam("state", state)
                .queryParam("redirect_uri", "http://localhost:8080/authorization/code").build().toUri();

        request.getSession().setAttribute("state", state);

        response.sendRedirect(uri.toString());
    }

    @GetMapping("/authorization/code")
    public void authorizationCode(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        Object sessionState = request.getSession().getAttribute("state");

        if (!state.equals(sessionState)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String body = webclient
                .post().uri(githubAccessTokenURL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", basicAuthorization("Ov23li7gAKPzcEAVVsgs", "SECRET"))
                .body(fromFormData("code", code))
                .retrieve().toEntity(String.class)
                .block().getBody();

        System.out.println(body);

        AccessTokenResponse accessTokenResponse = objectMapper.readValue(body, AccessTokenResponse.class);

        request.getSession().setAttribute("access-token", accessTokenResponse.accessToken());
    }

    String basicAuthorization(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(UTF_8));
        return "Basic " + new String(encodedAuth, UTF_8);
    }
}
