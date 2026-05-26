package info.ejava.examples.db.jpa.songs;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class JPASongsApp {

    public static void main(String ... args) {
        SpringApplication.run(JPASongsApp.class, args);
    }

    @Component
    @DependsOnDatabaseInitialization
    public class LogInfo implements CommandLineRunner {

        @Autowired
        private DataSource ds;
        @Autowired
        private EntityManagerFactory emf;
        @Autowired
        private EntityManager em;
        @Override
        public void run(String... args) throws Exception {
            log.info("dbUrl = {} ", ds.getConnection().getMetaData().getURL());      
            log.info("persistence unit = {} ", emf);
            log.info("persistence context = {} ", em);
        }
        
    }

    @Bean
    @ConditionalOnProperty(prefix = "db", name = "populate", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner populate(EntityManager em, SongDTOFactory dtoFactory, SongsRepository songsRepo, SongsMapper mapper){
        return (args) -> {
            List<SongDTO> dtos =  dtoFactory.listBuilder().songs(100, 100);
            List<Song> songBOs = dtos.stream().map(dto->mapper.map(dto)).toList();
            songsRepo.saveAll(songBOs);

            int count = em.createQuery("select count(s) from Song s",Number.class).getSingleResult().intValue();
            log.info("we have {} songs ", count);
        };
    }


    
}
