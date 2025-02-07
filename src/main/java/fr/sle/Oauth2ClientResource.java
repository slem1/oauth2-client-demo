package fr.sle;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/")
public class Oauth2ClientResource {

    private final URI githubAuthorizeURL;
    private final URI githubAccessTokenURL;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public Oauth2ClientResource(RestClient restClient, ObjectMapper objectMapper) throws URISyntaxException {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        githubAuthorizeURL = new URI("https", "github.com", "/login/oauth/authorize", null);
        githubAccessTokenURL = new URI("https", "github.com", "/login/oauth/access_token", null);
    }

    //https://github.com/login/oauth/authorize?client_id=123456&state=ABC&redirect_uri=http://localhost:8080/authorization/code
    @RequestMapping("/login")
    public void login(HttpServletResponse response) {

    }


//    @RequestMapping("/authorization/code")
//    public void exchangeCode(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletRequest request) {
//
//    }

    String basicAuthorization(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(UTF_8));
        return "Basic " + new String(encodedAuth, UTF_8);
    }
}
