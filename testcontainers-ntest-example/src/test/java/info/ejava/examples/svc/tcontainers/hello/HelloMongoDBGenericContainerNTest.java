package info.ejava.examples.svc.tcontainers.hello;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={TestcontainersHelloApp.class,
        ClientNTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@Testcontainers //manages lifecycle of container
@Slf4j
class HelloMongoDBGenericContainerNTest {
    @Container
    //https://github.com/testcontainers/testcontainers-java/issues/4695#issuecomment-1046086243 Option1
/* This alternate form of starting mongoDB is very similar to the security-enabled, docker-compose configuration
used many times in the examples. Adding real values for the admin username/password, triggers the container
to activate security and require user credentials.
*/
    private static GenericContainer mongoDB = new GenericContainer("mongo:4.4.0-bionic")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "secret")
            .withExposedPorts(27017);
    @DynamicPropertySource
    private static void addLateSpringContextProperties(DynamicPropertyRegistry registry) {
        String userName = (String)mongoDB.getEnvMap().get("MONGO_INITDB_ROOT_USERNAME");
        String password = (String)mongoDB.getEnvMap().get("MONGO_INITDB_ROOT_PASSWORD");
        registry.add("spring.data.mongodb.uri",()->
            ClientNTestConfiguration.mongoUrl(userName, password,
                    mongoDB.getHost(), mongoDB.getMappedPort(27017), "testcontainers"));
    }

    @Value("${spring.data.mongodb.uri}")
    private String expectedMongoUrl;
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private RestTemplate anonymousUser;
    private UriBuilder dbUrl;

    @BeforeEach
    void init(@LocalServerPort int port) {
        dbUrl = UriComponentsBuilder.fromHttpUrl("http://localhost").port(port).path("/api/hello/{db}");
    }

    @Test
    void can_populate_spring_context_with_dynamic_properties() {
        then(expectedMongoUrl).matches("mongodb://.*(?:localhost|host.docker.internal):[0-9]+/testcontainers\\?authSource=admin");
    }

    @Test
    void can_get_connection() {
        //given
        then(mongoClient).isNotNull();
        //when
        String shortDescription = mongoClient.getClusterDescription().getShortDescription();
        //then
        log.info("shortDescription={}", shortDescription);
        new MongoVerifyTest().actual_hostport_matches_expected(expectedMongoUrl, shortDescription);
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
        new MongoVerifyTest().actual_hostport_matches_expected(expectedMongoUrl, shortDescription);
    }
}
