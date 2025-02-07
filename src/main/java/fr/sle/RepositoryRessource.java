package fr.sle;

import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class RepositoryRessource {

    private final RestClient restClient;
    private final URI githubRepositoriesURL;

    public RepositoryRessource(RestClient restClient) throws URISyntaxException {
        this.restClient = restClient;
        githubRepositoriesURL = new URI("https", "api.github.com", "/user/repos", null);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> repositories(HttpServletRequest request) {
        Object accessToken = request.getSession().getAttribute("access-token");
        System.out.println(accessToken);

        ResponseEntity<String> response = restClient.get()
                .uri(githubRepositoriesURL)
                .accept(MediaType.parseMediaType("application/vnd.github+json"))
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (request1, response1) -> {
                })
                .toEntity(String.class);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } else if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected Error occured");
        } else {
            return ResponseEntity.ok(response.getBody());
        }
    }
}
