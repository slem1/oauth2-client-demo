package fr.sle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    String basicAuthorization(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(UTF_8));
        return "Basic " + new String(encodedAuth, UTF_8);
    }
}
