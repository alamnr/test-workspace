package info.ejava.examples.svc.tcontainers.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={TestcontainersHelloApp.class,
        ClientNTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
//prevent MongoDB autoconfig errors -- not part of this test
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@Testcontainers //manages lifecycle of container
@Slf4j
class HelloPostgresTestcontainerNTest {
    @Container
    private static PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:12.3-alpine");
    @DynamicPropertySource
    private static void addLateSpringContextProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",()->postgres.getJdbcUrl());
        registry.add("spring.datasource.username", ()->postgres.getUsername());
        registry.add("spring.datasource.password", ()->postgres.getPassword());
    }
    @Value("${spring.datasource.url}")
    private String expectedJdbcUrl;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private RestTemplate anonymousUser;
    private UriBuilder dbUrl;

    @BeforeEach
    void init(@LocalServerPort int port) {
        dbUrl = UriComponentsBuilder.fromHttpUrl("http://localhost").port(port).path("/api/hello/{db}");
    }

    @Test
    void can_populate_spring_context_with_dynamic_properties() {
        then(expectedJdbcUrl).matches("jdbc:postgresql://(?:localhost|host.docker.internal):[0-9]+/test.*");
    }

    @Test
    void can_get_connection() throws SQLException {
        //given
        then(dataSource).isNotNull();
        Connection conn=dataSource.getConnection();
        //when
        String jdbcUrl;
        try (conn) {
            jdbcUrl=conn.getMetaData().getURL();
        }
        //then
        log.info("jdbcUrl={}", jdbcUrl);
        then(jdbcUrl).isEqualTo(expectedJdbcUrl);
        then(jdbcUrl).contains("jdbc:postgresql");
    }

    @Test
    void server_can_get_jdbc_connection() {
        //given
        URI url = dbUrl.build("jdbc");
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = anonymousUser.exchange(request, String.class);
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jdbcUrl=response.getBody();
        log.info("jdbcUrl={}", jdbcUrl);
        then(jdbcUrl).isEqualTo(expectedJdbcUrl);
        then(jdbcUrl).contains("jdbc:postgresql");
    }
}
