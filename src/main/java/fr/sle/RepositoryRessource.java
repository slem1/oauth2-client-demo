package fr.sle;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.ResponseEntity.status;

@RestController
public class RepositoryRessource {

    private final WebClient webclient;
    private final URI githubRepositoriesURL;

    public RepositoryRessource(WebClient webclient) throws URISyntaxException {
        this.webclient = webclient;
        githubRepositoriesURL = new URI("https", "api.github.com", "/user/repos", null);
    }

    @GetMapping("/")
    public ResponseEntity<String> repositories(HttpServletRequest request) {
        Object accessToken = request.getSession().getAttribute("access_token");
        System.out.println(accessToken);
        return webclient.get()
                .uri(githubRepositoriesURL)
                .accept(MediaType.parseMediaType("application/vnd.github+json"))
//                .header("Authorization", "Bearer " + accessToken)
                .exchangeToMono(r -> {
                    if (r.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.just(status(HttpStatus.UNAUTHORIZED).body("Unauthorized"));
                    } else if (r.statusCode().is4xxClientError()) {
                        return Mono.just(status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected Error occured"));
                    } else {
                        return r.toEntity(String.class);
                    }
                }).block();
    }
}
