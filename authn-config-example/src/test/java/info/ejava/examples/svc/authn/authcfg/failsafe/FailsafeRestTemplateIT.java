package info.ejava.examples.svc.authn.authcfg.failsafe;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.net.URI;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * We start with a familiar looking JUnit test and @SpringBootTest. We can still leverage a Spring Context, 
 * however, there is no application or server-side resources in the Spring context. The application has its Spring 
 * Context started by the Spring Boot Maven Plugin. This Spring Context is for the tests running within the Failsafe Plugin.
 */

// no application class in this integration test. Everything is server-side.
// have only a client-side web environment. No listen port necessary 

@SpringBootTest(classes = ClientConfigurationFailSafe.class, 
                webEnvironment = SpringBootTest.WebEnvironment.NONE ) 
@Slf4j
public class FailsafeRestTemplateIT {

    @Autowired
    private RestTemplate authnUser;
    @Autowired
    private URI authnUrl;


    /**
     * Since Maven integration tests have no RANDOM_PORT and no late @LocalServerPort injection, 
     * bean factories for components that depend on the server port do not require @Lazy instantiation.
    */ 
    @BeforeEach
    public void setUp() {
        log.info("baseUrl={}", authnUrl);
    }

    @Test
    public void user_can_call_authenticated() {
        //given a URL to an endpoint that accepts only authenticated calls
        URI url = UriComponentsBuilder.fromUri(authnUrl).queryParam("name", "jim").build().toUri();

        //when called with an authenticated identity
        ResponseEntity<String> response = authnUser.getForEntity(url, String.class);

        //then expected results returned
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(response.getBody()).contains("hello, jim");
        BDDAssertions.then(response.getBody()).isEqualTo("hello, jim :caller=user");
        
    }
}


