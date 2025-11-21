package info.ejava.examples.svc.tcontainers.hello;

import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={TestcontainersHelloApp.class,
        ClientNTestConfiguration.class},
        properties={"spring.datasource.url=jdbc:h2:mem:testcontainers"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@Slf4j
class HelloH2InMemoryNTest {
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
    void can_get_connection() throws SQLException {
        //given
        then(dataSource).isNotNull();
        String jdbcUrl;
        //when
        try(Connection conn=dataSource.getConnection()) {
            jdbcUrl=conn.getMetaData().getURL();
        }
        //then
        log.info("jdbcUrl={}", jdbcUrl);
        then(jdbcUrl).isEqualTo(expectedJdbcUrl);
        then(jdbcUrl).startsWith("jdbc:h2:mem");
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
        then(jdbcUrl).contains("jdbc:h2:mem");
    }
}
