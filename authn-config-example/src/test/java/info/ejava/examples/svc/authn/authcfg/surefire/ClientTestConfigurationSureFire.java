package info.ejava.examples.svc.authn.authcfg.surefire;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;

/*
* A test configuration used by remote IT test client
*/

@TestConfiguration (proxyBeanMethods = false)
@Profile("ntest")
public class ClientTestConfigurationSureFire {
    
    // @Bean
    // @ConfigurationProperties("it.server")
    // public ServerConfig itServerConfig(){
    //     return new ServerConfig();
    // }

    @Bean 
    ClientHttpRequestFactory requestFactory(){
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory){
        RestTemplate restTemplate = builder.requestFactory(
            // used to read the stream twice -- so we can use the logging filter below
            ()-> new BufferingClientHttpRequestFactory(requestFactory))
            .interceptors(new RestTemplateLoggingFilter())
            .build();
            return restTemplate;
    }

    @Bean
    public RestTemplate authnUserNTest(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory) {
        RestTemplate restTemplate = builder.requestFactory(
             // used to read the stream twice -- so we can use the logging filter below
             ()-> new BufferingClientHttpRequestFactory(requestFactory))
             .interceptors(new BasicAuthenticationInterceptor("user", "password"),new RestTemplateLoggingFilter())
             .build();
        return restTemplate;        
    }

    // Construct a Restclient from a RestClient.Builder

    @Bean
    public RestClient anonymousUserClient(RestClient.Builder builder, ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(//used to read streams twice -- to use logging filter
                        new BufferingClientHttpRequestFactory(requestFactory))
                .requestInterceptor(new RestTemplateLoggingFilter())
                .build();
    }

    @Bean
    public RestClient authnUserClient(RestClient.Builder builder, ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(//used to read streams twice -- to use logging filter
                        new BufferingClientHttpRequestFactory(requestFactory))
                .requestInterceptors((list)->{
                    list.add(new BasicAuthenticationInterceptor("user", "password"));
                    list.add(new RestTemplateLoggingFilter());
                })
                .build();
    }

        /**
         * Construct a RestClient from a configured RestTemplate
         */
    //    @Bean
    //    public RestClient authnUserClient(RestTemplate authnUser) {
    //        return RestClient.create(authnUser);
    //    }

}
