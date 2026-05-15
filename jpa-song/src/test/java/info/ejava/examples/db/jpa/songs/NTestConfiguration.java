package info.ejava.examples.db.jpa.songs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;

@TestConfiguration
public class NTestConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SongDTOFactory dtoFactory() {
        return new SongDTOFactory();
    }
    
}
