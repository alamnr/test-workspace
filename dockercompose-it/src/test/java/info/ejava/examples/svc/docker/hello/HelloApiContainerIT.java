package info.ejava.examples.svc.docker.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes=ClientTestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({"test","it"})
//we have project Mongo dependency but don't have or need Mongo for this remote client
@EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@Slf4j
class HelloApiContainerIT {
    @Autowired
    private RestTemplate anonymousUser;
    private UriComponentsBuilder helloDBUrl;

    @BeforeEach
    void init(@Autowired URI baseUrl) throws InterruptedException {
        log.info("baseUrl={}", baseUrl);
        helloDBUrl = UriComponentsBuilder.fromUri(baseUrl).path("api/hello/{db}");

        URI healthUrl = UriComponentsBuilder.fromUri(baseUrl).path("actuator/health").build().toUri();
        waitFor(healthUrl, Duration.of(120, ChronoUnit.SECONDS));
    }

    boolean waitFor(URI waitForUrl, Duration maxWait) {
        long startTime = System.currentTimeMillis();
        do {
            try {
                anonymousUser.getForEntity(waitForUrl, String.class);
                return true;
            } catch (RestClientException ex) {
                log.info("waiting for API {}: {}; {} <=? {}", waitForUrl, ex.toString(), System.currentTimeMillis()-startTime, maxWait.toMillis());
                try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        } while (System.currentTimeMillis()-startTime < maxWait.toMillis());
        return false;
    }

    @Test
    void server_can_get_jdbc_connection() {
        //given
        URI url = helloDBUrl.build("jdbc");
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        String jdbcUrl = anonymousUser.exchange(request, String.class).getBody();
        //then
        log.info("jdbcUrl={}", jdbcUrl);
        //hostname will be postgres and port will be default internal 5332
        then(jdbcUrl).contains("jdbc:postgresql","postgres:5432");
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
        //hostname will be mongo and port will be default internal 27017
        then(shortDescription).contains("address=mongodb:27017");
    }
}
