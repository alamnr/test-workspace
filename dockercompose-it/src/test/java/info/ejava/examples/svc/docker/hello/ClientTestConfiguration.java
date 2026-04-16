package info.ejava.examples.svc.docker.hello;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * A test configuration used by local NTests and remote IT test clients.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@EnableAutoConfiguration
public class ClientTestConfiguration {
    //use for IT tests
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

    //use for NTest and IT tests
    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder,
                                      ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
    }

    //use for NTest and IT tests
    @Bean
    public ClientHttpRequestFactory httpsRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }
}
