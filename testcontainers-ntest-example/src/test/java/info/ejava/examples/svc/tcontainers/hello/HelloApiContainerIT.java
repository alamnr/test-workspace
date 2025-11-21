package info.ejava.examples.svc.tcontainers.hello;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= ClientNTestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({"test"})
@EnableAutoConfiguration
@Testcontainers //manages lifecycle for @Containers
@Slf4j
class HelloApiContainerIT {
    @Container
    private static DockerComposeContainer network = ClientNTestConfiguration.testEnvironment();
    @DynamicPropertySource
    private static void addLateSpringContextProperties(DynamicPropertyRegistry registry) {
        ClientNTestConfiguration.initProperties(registry, network);
    }

    @Autowired
    private RestTemplate anonymousUser;
    private UriBuilder dbUrl;

    @Autowired //optional -- just demonstrating we have access to DB
    private DataSource dataSource;
    @Autowired //optional -- just demonstrating we have access to DB
    private MongoClient mongoClient;

    @BeforeEach
    void init(@Value("${it.server.host:localhost}") String remoteApiContainerHost,
              @Value("${it.server.port:9090}") int remoteApiContainerPort) {
        dbUrl=UriComponentsBuilder.fromHttpUrl("http://localhost")
                .host(remoteApiContainerHost)
                .port(remoteApiContainerPort)
                .path("/api/hello/{db}");
    }

    @Test
    void dataSource_can_provide_connection() throws SQLException {
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
        then(jdbcUrl).contains("jdbc:postgresql")
                .doesNotContain("5432"); //test uses socat proxy
    }

    @Test
    void server_can_get_jdbc_connection() {
        //given
        URI url = dbUrl.build("jdbc");
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        String jdbcUrl = anonymousUser.exchange(request, String.class).getBody();
        //then
        log.info("jdbcUrl={}", jdbcUrl);
        //hostname will be postgres and port will be default internal 5432
        then(jdbcUrl).contains("jdbc:postgresql","postgres:5432");
    }

    @Test
    void mongoClient_can_get_connection() {
        //given
        then(mongoClient).isNotNull();
        //then
        then(mongoClient.getClusterDescription().getShortDescription())
                .doesNotContain("27017");
        then(mongoClient.listDatabaseNames()).contains("admin");
    }

    @Test
    void server_can_get_mongo_connection() {
        //given
        URI url = dbUrl.build("mongo");
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        String shortDescription = anonymousUser.exchange(request, String.class).getBody();
        //then
        log.info("shortDescription={}", shortDescription);
        //hostname will be mongo and port will be default internal 27017
        then(shortDescription).contains("address=mongodb:27017");
    }
}
