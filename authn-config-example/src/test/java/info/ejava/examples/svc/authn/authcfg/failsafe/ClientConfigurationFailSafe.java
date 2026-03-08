package info.ejava.examples.svc.authn.authcfg.failsafe;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 *  A test configuration by remote IT test clients
 */

/**
 *  The @SpringBootTest will demand to have a @SpringBootConfiguration (@SpringBootApplication is a @SpringBootConfiguration) to initialize 
 * the application hosting the test. So, we will assign that annotation to our test’s configuration. We also need to add
 * @EnableAutoConfiguration (normally supplied by @SpringBootApplication) to enable injection of external resources like RestTemplateBuilder.
 */

@SpringBootConfiguration(proxyBeanMethods = false) // there must be 1 @SpringBootConfiguration and must be supplied when running without a @SpringBootApplication
@EnableAutoConfiguration 	// must enable AutoConfiguration to trigger RestTemplateBuilder and other automatic resources
@Slf4j
public class ClientConfigurationFailSafe {
    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @ConditionalOnMissingBean
    @Bean
    @ConfigurationProperties("it.server") // inject properties for test (e.g., it.server.port) from Failsafe Maven Plugin
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    
    @Bean
    public URI authnUrl(ServerConfig itServerConfig) {  
        // 	injectable baseUrl of remote server
        URI baseUrl = itServerConfig.getBaseUrl();
        log.info("baseUrl={}", baseUrl);
        return UriComponentsBuilder.fromUri(baseUrl).path("/api/authn/hello").build().toUri();
    }

    @Bean
    public ClientHttpRequestFactory httpsRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate authnUser(RestTemplateBuilder builder,
                                  ClientHttpRequestFactory requestFactory) { 
        //injectable RestTemplate with authentication and HTTP filters applied
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new BasicAuthenticationInterceptor(username, password),
                        new RestTemplateLoggingFilter())
                .build();
        return restTemplate;
    }



}
