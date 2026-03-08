package info.ejava.examples.svc.authn.authcfg.surefire;

import java.net.URI;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.web.ServerConfig;

@SpringBootTest(classes = ClientTestConfigurationSureFire.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "test=true" )
public class AuthnRestTemplateNTest {

    @Autowired
    private RestTemplate anonymousUser;
    @Autowired
    private RestTemplate authnUserNTest;

    private URI baseUrl;
    private URI anonymousUrl;
    private URI authnUrl;

    @BeforeEach
    public void setUp(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        baseUrl = serverConfig.getBaseUrl();
        anonymousUrl = UriComponentsBuilder.fromUri(baseUrl).path("/api/anonymous/hello").build().toUri();
        authnUrl = UriComponentsBuilder.fromUri(baseUrl).path("/api/authn/hello").build().toUri();
    }

    @Test
    void anonymous_can_access_static_content() {
        // given
        URI url = UriComponentsBuilder.fromUri(baseUrl).path("/content/hello_static.txt").build().toUri();
        // when
        ResponseEntity<String> response = anonymousUser.getForEntity(url, String.class);
        // then
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(response.getBody()).startsWith("Hello, static file");
    }

    @Test
    public void anonymous_can_call_unauthenticated() {
        // given a url to end point that takes anonymous call
        URI url = UriComponentsBuilder.fromUri(anonymousUrl).queryParam("name", "jim").build().toUri();

        // when callled with no identity
        ResponseEntity<String> resp = anonymousUser.getForEntity(url, String.class);

        // then

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getBody()).isEqualTo("hello, jim :caller=(null)");
    }

    @Test
    public void anonymous_cannot_call_authenticated() {
        //given a URL to an endpoint that accepts only authenticated calls
        URI url = UriComponentsBuilder.fromUri(authnUrl).queryParam("name", "jim").build().toUri();

        //when called with no identity
        HttpClientErrorException ex = BDDAssertions.catchThrowableOfType(
                ()-> anonymousUser.getForEntity(url, String.class),
                HttpClientErrorException.Unauthorized.class);

        //then expected results returned
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void user_can_call_authenticated() {
        //given a URL to an endpoint that accepts only authenticated calls
        URI url = UriComponentsBuilder.fromUri(authnUrl).queryParam("name", "jim").build().toUri();

        //when called with an authenticated identity
        ResponseEntity<String> response = authnUserNTest.getForEntity(url, String.class);

        //then expected results returned
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(response.getBody()).isEqualTo("hello, jim :caller=user");
    }


    
}
