package info.ejava.examples.db.jpa.songs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import info.ejava.examples.db.jpa.songs.dto.ISODateFormat;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class JpaSongsApp {
    
    public static void main(String ... args) {
        SpringApplication.run(JpaSongsApp.class, args);
    }

    @Component
    public class LogInfo implements CommandLineRunner {
        @Autowired
        private DataSource ds;

        @Override
        public void run(String... args) throws Exception {
            log.info("dbUrl = {}",ds.getConnection().getMetaData().getURL());
        }
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Jackson2ObjectMapperBuilderCustomizer jacksonMapper() {
        return (builder) -> builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                            .createXmlMapper(false)
                            .dateFormat(new ISODateFormat());
    }
}
