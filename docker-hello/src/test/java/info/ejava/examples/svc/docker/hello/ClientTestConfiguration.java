package info.ejava.examples.svc.docker.hello;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * A test configuration used by remote IT test clients.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@EnableAutoConfiguration
@Slf4j
public class ClientTestConfiguration {
    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    @ConfigurationProperties(prefix = "it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    //use for IT tests
    @Bean
    public URI baseUrl(ServerConfig serverConfig) {
        URI baseUrl = serverConfig.build().getBaseUrl();
        return baseUrl;
    }

    @Bean
    public String authnUsername() { return username; }


    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder,
                                      ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
    }

    @Bean
    public RestTemplate authnUser(RestTemplateBuilder builder,
                                  ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(
                        new BasicAuthenticationInterceptor(username, password),
                        new RestTemplateLoggingFilter()
                )
                .build();
    }

    @Bean
    public ClientHttpRequestFactory httpsRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }
}
