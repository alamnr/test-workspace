package info.ejava.examples.db.jpa.songs.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = {NTestConfiguration.class}, properties = "db.populate=false")
//@ActiveProfiles(profiles = "test")
@Tag("springboot")
@Slf4j
@DisplayName("repositorycrudmethod")
public class SongsCrudRepositoryMethodsNTest {
    
    @Autowired
    private SongsRepository  songsRepo;
    @Autowired
    private SongsMapper mapper;
    @Autowired 
    private SongDTOFactory dtoFactory;
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @BeforeEach
    void setUp() {
        log.info("dbUrl = {}", dbUrl);
        songsRepo.deleteAll();
    }

    @Test
    void save_new() {
        // given an entity instance
        Song song = mapper.map(dtoFactory.make());
        BDDAssertions.assertThat(song.getId()).isZero();

        // when persisting
        songsRepo.save(song);


        // then - entity is persisted
        BDDAssertions.then(song.getId()).isNotZero();        
    }

    @Test
    void save_update() {
        // given an entity instance
        Song song = mapper.map(dtoFactory.make());
        songsRepo.save(song);
        songsRepo.flush();
        Song updatedSong = Song.builder().id(song.getId())
                                .title("new title")
                                .artist(song.getArtist())
                                .released(song.getReleased())
                                .build();
        
        // when persisting update
        songsRepo.save(updatedSong);

        // then  entity is persisted
        BDDAssertions.then(songsRepo.findOne(Example.of(updatedSong))).isPresent();


    }

    @Test
    void exists() {
        // given a transient entity to be persisted
        Song pojoSong = mapper.map(dtoFactory.make());
        songsRepo.save(pojoSong);

        // when 

        boolean exists = songsRepo.existsById(pojoSong.getId());
        // select count(*) as col_0_0_ from reposongs_song song0_ where song0_.id = ?

        // then
        BDDAssertions.then(exists).isTrue();

    }

    @Test
    void findById_found() {
        //given a persisted entity instance
        Song pojoSong = mapper.map(dtoFactory.make());
        songsRepo.save(pojoSong);

        //when - finding the existing entity
        Optional<Song> result = songsRepo.findById(pojoSong.getId());
        //select ...
        //from reposongs_song song0_
        //where song0_.id=?

        //then
        BDDAssertions.then(result).isPresent();

        //when - obtaining the instance
        Song dbSong = result.get();

        //then - database copy matches initial POJO
        BDDAssertions.then(dbSong).isNotNull();
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(pojoSong.getArtist());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(pojoSong.getTitle());
        //the dbSong instance is coming from DB
        //comparing SQL Timestamp to java.util.Date

        BDDAssertions.then(pojoSong.getReleased()).isEqualTo(dbSong.getReleased());
    }

     @Test
    void findById_not_found() {
        //given - an ID that does not exist
        int missingId = 123456;

        //when - using find for a missing ID
        Optional<Song> result = songsRepo.findById(missingId);

        //then - the optional can be benignly tested
        BDDAssertions.then(result).isNotPresent();

        //then - the optional is asserted during the get()
        BDDAssertions.assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);
    }

     @Test
    void saveAll_entities() {
        //given - several songs persisted
        Collection<Song> songs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .toList();

        //when
        songsRepo.saveAll(songs);

        //then - each will exist in the DB
        songs.forEach(s->{
            BDDAssertions.then(songsRepo.existsById(s.getId())).isTrue();
        });
    }

    @Test
    void findAll_entities() {
        // given - several song persisted
        Collection<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                                        .map(dto -> mapper.map(dto))
                                        .toList();
        songsRepo.saveAll(pojoSongs);
        Map<Integer,Song> pojoSongsMap = pojoSongs.stream()
                                            .collect(Collectors.toMap(s->s.getId(), s->s));

        // when
        Iterable<Song> result = songsRepo.findAll();

        // then we can find each instance
        BDDAssertions.then(result).hasSameSizeAs(pojoSongs);
        BDDAssertions.then(result).allMatch(s->pojoSongsMap.containsKey(s.getId()));
    }

     @Test
    //@Transactional - without this, the entity will get loaded
    void delete_exists() {
        //given - a persisted entity instance
        Song existingSong = mapper.map(dtoFactory.make());
        songsRepo.save(existingSong);

        //when - deleting an existing instance
        songsRepo.delete(existingSong);
        //select ... as title4_0_0_ from reposongs_song song0_ where song0_.id=?
        //delete from reposongs_song where id=?

        //then - instance will be removed from DB
        BDDAssertions.then(songsRepo.existsById(existingSong.getId())).isFalse();
    }

    @Test
    void delete_not_exists() {
        //given - a persisted entity instance
        Song doesNotExist = mapper.map(dtoFactory.make( SongDTOFactory.oneUpId));
        BDDAssertions.then(songsRepo.existsById(doesNotExist.getId())).isFalse();

        //when - deleting a non-existing instance
        songsRepo.delete(doesNotExist);
        //select ... as title4_0_0_ from reposongs_song song0_ where song0_.id=?
        //no exception was thrown
    }

    @Test
    //@Transactional
    void deleteById_exists() {
        //given - a persisted entity instance
        Song existingSong = mapper.map(dtoFactory.make());
        songsRepo.save(existingSong);

        //when - deleting an existing instance
        songsRepo.deleteById(existingSong.getId());

        //then - instance will be removed from DB
        BDDAssertions.then(songsRepo.existsById(existingSong.getId())).isFalse();
    }

    @Test
    void deleteById_not_exists() {
        //given - an ID that does not exist
        int missingId = 123456;

        //when - deleting a non-existant instance
        //Spring Boot <= 3.0.6
        Throwable ex= BDDAssertions.catchThrowable(()->{
            songsRepo.deleteById(missingId);
        });

        //then -- <= Spring Boot 3.0.6 exception is thrown
        //then(ex).isInstanceOf(EmptyResultDataAccessException.class);
        //then -- >= Spring Boot > 3.1.0 ignored
        BDDAssertions.then(ex).isNull();
        log.info("deleted non-existant ID {}", missingId, ex);
    }

    @Test
    void deleteAll_every() {
        // given
        Collection<Song> pojosongs = dtoFactory.listBuilder().songs(3, 3).stream()
                                            .map(dto->mapper.map(dto))
                                            .toList();
        songsRepo.saveAll(pojosongs);

        // when
        songsRepo.deleteAll();

        // then
        BDDAssertions.then(pojosongs).allSatisfy(s->BDDAssertions.then(songsRepo.existsById(s.getId())).isFalse());
    }

    @Test
    void deleteAll_some() {
        // given
        List<Song> pojosongs = new ArrayList<>();

        String titlePrefix = "123";
        UnaryOperator<SongDTO> addTitlePrefix = s -> {
            s.setTitle(titlePrefix + s.getTitle());
            return s;
        };
        IntStream.range(1, 0).forEach(o -> {
            Song song = mapper.map(dtoFactory.make(SongDTOFactory.nextDate,addTitlePrefix));
            pojosongs.add(song);
        });
      
        songsRepo.saveAll(pojosongs);
        log.info("======================== size - {} ", pojosongs.size());
        List<Song> toDelete = IntStream.range(0, pojosongs.size()).mapToObj(i->pojosongs.get(i)).toList();

        // when deleting a subset
        songsRepo.deleteAll(toDelete);

        // then
        toDelete.forEach(song -> BDDAssertions.then(songsRepo.existsById(song.getId())).isFalse());
        // BDDAssertions.then(songsRepo.existsById(pojosongs.get(0).getId())).isFalse();
        // BDDAssertions.then(songsRepo.existsById(pojosongs.get(1).getId())).isFalse();
        // BDDAssertions.then(songsRepo.existsById(pojosongs.get().getId())).isFalse();
  

    }

     @Test
    void count() {
        //given
        List<Song> pojoSongs = dtoFactory.listBuilder().songs(3, 3).stream()
                .map(dto->mapper.map(dto))
                .toList();
        songsRepo.saveAll(pojoSongs);

        //when
        long songCount = songsRepo.count();

        //then
        BDDAssertions.then(songCount).isEqualTo(pojoSongs.size());
    }

    @Test
    @Transactional
    void save_modify_existing() {
        //given - a persisted entity instance
        Song song = mapper.map(dtoFactory.make());
        songsRepo.save(song);
        String originalTitle = song.getTitle();
        String modifiedTitle = dtoFactory.title() + "1";
        BDDAssertions.assertThat(originalTitle).as("given titles").isNotEqualTo(modifiedTitle);

        //when - modifying song instance without saving
        song.setTitle(modifiedTitle);

        //then - DB is modified because we have a transaction active on @Test method
        Song dbSong = songsRepo.findById(song.getId()).get();
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(modifiedTitle);
    }



}
