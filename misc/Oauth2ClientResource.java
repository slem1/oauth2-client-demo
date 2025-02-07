package fr.sle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
    public void login(HttpServletResponse response) throws IOException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(githubAuthorizeURL);
        builder.queryParam("client_id", "Ov23liA7OIUNaSsz7PBo");
        builder.queryParam("state", "123456");
        builder.queryParam("redirect_uri", "http://localhost:8080/authorization/code");

        response.sendRedirect(builder.toUriString());

    }


    @RequestMapping("/authorization/code")
    public void exchangeCode(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletRequest request) {

        if (!state.equals("123456")) {
            throw new RuntimeException("Invalid state");
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("code", code);

        AccessTokenResponse accessTokenResponse = restClient.post().uri(githubAccessTokenURL)
                .contentType(APPLICATION_FORM_URLENCODED)
                .accept(APPLICATION_JSON)
                .header("Authorization", basicAuthorization("Ov23liA7OIUNaSsz7PBo", "6b4439e1c0070da8d8fbcce8fe0ad1833e5d221f"))
                .body(body)
                .retrieve().body(AccessTokenResponse.class);

        System.out.println(accessTokenResponse);

        request.getSession().setAttribute("access-token", accessTokenResponse.accessToken());

    }

    String basicAuthorization(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(UTF_8));
        return "Basic " + new String(encodedAuth, UTF_8);
    }
}
