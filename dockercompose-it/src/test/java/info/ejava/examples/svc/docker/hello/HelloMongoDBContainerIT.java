package info.ejava.examples.svc.docker.hello;

import com.mongodb.client.MongoClient;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.PropertiesMongoConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={DockerComposeHelloApp.class,
        ClientTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//uncomment containerdb-dev when developing/running against a fixed instance
//@ActiveProfiles({"test","containerdb"/*,"containerdb-dev"*/})
@ActiveProfiles({"test","containerdb","containerdb-dev"})
@Slf4j
class HelloMongoDBContainerIT {
    @Value("${spring.data.mongodb.uri}")
    private String expectedMongoUrl;
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    PropertiesMongoConnectionDetails mongoProperties;
    @Autowired
    private RestTemplate anonymousUser;
    private UriComponentsBuilder helloDBUrl;

    @BeforeEach
    void init(@LocalServerPort int port) {
        URI baseUrl = new ServerConfig().withPort(port).build().getBaseUrl();
        log.info("baseUrl={}", baseUrl);
        helloDBUrl = UriComponentsBuilder.fromUri(baseUrl).path("api/hello/{db}");
    }

    @Test
    void can_get_connection() {
        //given
        then(mongoClient).isNotNull();
        //when
        String shortDescription = mongoClient.getClusterDescription().getShortDescription();
        String connectionString=mongoProperties.getConnectionString().getConnectionString();
        //then
        log.info("connectionString={}", connectionString);
        then(connectionString).isEqualTo(expectedMongoUrl);
        log.info("shortDescription={}", shortDescription);
        new MongoVerifyTest().actual_hostport_matches_expected(expectedMongoUrl, shortDescription);
        then(mongoClient.listDatabaseNames()).contains("admin");
    }

    @Test
    void server_can_get_mongo_connection() {
        //given
        URI url = helloDBUrl.build("mongo");
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        String shortDescription = anonymousUser.exchange(request, String.class).getBody();
        //then
        log.info("shortDescription={}", shortDescription);
        new MongoVerifyTest().actual_hostport_matches_expected(expectedMongoUrl, shortDescription);
    }
}
