package info.ejava.examples.db.mongo.books;

import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class NTestConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BookDTOFactory dtoFactory() {
        return new BookDTOFactory();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .exchangeStrategies(ExchangeStrategies.builder().codecs(conf->{
                    conf.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    conf.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                }).build())
                .build();
    }

    @Bean
    public ClientHttpRequestFactory requestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.data.mongodb", name = "uri", matchIfMissing = false)
    @EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
    public class DisableEmbeddedMongoConfiguration {
    }
}
