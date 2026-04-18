package info.ejava.examples.svc.docker.hello;

import java.net.URI;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = {DockerComposeHelloApp.class,ClientTestConfiguration.class},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// uncomment cntainerdb-dev when running against a fixed instance
//@ActiveProfiles({"test","containerdb"/*,"containerdb-dev"*/})
@ActiveProfiles({"test","containerdb","containerdb-dev"})
@Slf4j
public class HelloPostgresContainerIT {
    
    @Value("${spring.datasource.url}")
    private String expectedJdbcUrl;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private RestTemplate anonymousUser;

    private UriComponentsBuilder helloDBUrl;

    @BeforeEach
    void init(@LocalServerPort int port){
        URI baseUrl = new ServerConfig().withPort(port).build().getBaseUrl();
        log.info("baseUrl={}", baseUrl);
        helloDBUrl = UriComponentsBuilder.fromUri(baseUrl).path("api/hello/{db}");
    }

    @Test
    void can_get_connection() throws SQLException {
        //given
        BDDAssertions.then(dataSource).isNotNull();
        //when
        String jdbcUrl = dataSource.getConnection().getMetaData().getURL();
        //then
        log.info("jdbcUrl={}", jdbcUrl);
        BDDAssertions.then(jdbcUrl).isEqualTo(expectedJdbcUrl);
        BDDAssertions.then(jdbcUrl).contains("jdbc:postgresql");
    }
     @Test
    void server_can_get_jdbc_connection() {
        //given
        URI url = helloDBUrl.build("jdbc");
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = anonymousUser.exchange(request, String.class);
        //then
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jdbcUrl=response.getBody();
        log.info("jdbcUrl={}", jdbcUrl);
        BDDAssertions.then(jdbcUrl).isEqualTo(expectedJdbcUrl);
        BDDAssertions.then(jdbcUrl).contains("jdbc:postgresql");
    }

}
