package fr.sle;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class ClientApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(ClientApp.class, args);
    }

    @Configuration
    static class ClientAppConfiguration{

        @Bean
        public WebClient createWebClient() {
            final WebClient webclient;
            final ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                    .build();
            webclient = WebClient.builder()
                    .exchangeStrategies(strategies)
                    .clientConnector((new ReactorClientHttpConnector(HttpClient.create().proxyWithSystemProperties()))).build();
            return webclient;
        }
    }
}
