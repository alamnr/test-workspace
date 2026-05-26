package info.ejava.examples.db.jpa.songs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = {SongsRepository.class}, repositoryImplementationPostfix = "Impl")
@EntityScan(basePackageClasses = {Song.class})
public class SongsConfiguration {
    
    
}
