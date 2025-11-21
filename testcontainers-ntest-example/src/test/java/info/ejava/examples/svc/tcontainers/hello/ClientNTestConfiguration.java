package info.ejava.examples.svc.tcontainers.hello;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class ClientNTestConfiguration {
    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder,
                                      ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
    }

    @Bean
    public ClientHttpRequestFactory httpsRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    
    public static DockerComposeContainer testEnvironment() {
        return new DockerComposeContainer("testcontainers-ntest",
                List.of(new File("src/main/docker/docker-compose.yml"),    //core wiring
                        new File("target/test-classes/docker-compose-test.yml"))) //build & port info
                .withBuild(true)
            .withExposedService("api", 8080)
            .withExposedService("postgres", 5432)
            .withExposedService("mongodb", 27017)
            .withLocalCompose(true) //false works when host mounts files, true works when not mounted
            //https://github.com/testcontainers/testcontainers-java/pull/5608
            .withOptions("--compatibility") //change dashes to underscores when using local=true
                                            //so testcontainers can find the container_name
            .withStartupTimeout(Duration.ofSeconds(120));
    }

    public static void initProperties(DynamicPropertyRegistry registry, DockerComposeContainer network) {
        //needed for @Tests to locate API Server
        registry.add("it.server.port", ()->network.getServicePort("api", 8080));
        registry.add("it.server.host", ()->network.getServiceHost("api", null));

        //optional -- only if @Tests directly access the DB
        registry.add("spring.data.mongodb.uri",()-> mongoUrl("admin", "secret",
                network.getServiceHost("mongodb", null),
                network.getServicePort("mongodb", 27017),
                "testcontainers"
        ));
        registry.add("spring.datasource.url",()->jdbcUrl(
                network.getServiceHost("postgres", null),
                network.getServicePort("postgres", 5432)
        ));
        registry.add("spring.datasource.driver-class-name",()->"org.postgresql.Driver");
        registry.add("spring.datasource.username",()->"postgres");
        registry.add("spring.datasource.password",()->"secret");
    }
    
    public static String mongoUrl(String userName, String password, String host, int port, String database) {
        return String.format("mongodb://%s:%s@%s:%d/%s?authSource=admin", userName, password, host, port, database);
    }
    public static String jdbcUrl(String host, int port) {
        return String.format("jdbc:postgresql://%s:%d/postgres", host, port);
    }

}
