package info.ejava.examples.db.jpa.songs.jpa;

import java.lang.reflect.Field;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dao.JpaSongDAO;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = NTestConfiguration.class)
//@ActiveProfiles(profiles = "test")
@Tag("springboot")
@Slf4j
public class JpaSongsNTest {
    
    @Autowired
    private JpaSongDAO jpaDao;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void cleanup() {
        jpaDao.deleteAll();
    }

    @Test
    void create() {
        // given 
        Song song = mapper.map(dtoFactory.make());

        // when 
        jpaDao.create(song);

        // then
        BDDAssertions.then(song.getId()).isNotZero();
        BDDAssertions.then(jpaDao.existsById(song.getId())).isNotNull();        
    }

    @Test
    void fingById_exists(){
        // given a persistene instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song);

        // when find
        Song dbSong = jpaDao.findById(song.getId());

        // then entity is persisted
        BDDAssertions.then(dbSong.getId()).isEqualTo(song.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(song.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(song.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(song.getReleased());
    }

    @Test
    void findById_does_not_exist(){
        // given a persistence instance
        int missingId = 12345;

        // when 
        Song dbSong = jpaDao.findById(missingId);

        // then
        BDDAssertions.then(dbSong).isNull();
    }

    @Test
    @Transactional
    void update_entity(){
        // given a persisted entity
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        String newTitle = dtoFactory.title();

        // when
        originalSong.setTitle(newTitle);
        jpaDao.flush();
        

        // then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(newTitle);


    }

    
    @Test

    void update_entity_without_transactional(){
        // given a persisted entity
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        String newTitle = dtoFactory.title();

        // when
        originalSong.setTitle(newTitle);
        jpaDao.update(originalSong);

        // then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(newTitle);


    }

    @Test
    void update_exists() {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        BDDAssertions.assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());
        log.info("update - {}, original- {}", updatedSong, originalSong);

        //when - updating
        updatedSong = jpaDao.update(updatedSong);

        //then - db has new state
        Song dbSong = jpaDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());

    }

    @Test
    void update_does_not_exist() {
        //given a persisted instance
        Song originalSong = mapper.map(dtoFactory.make());
        jpaDao.create(originalSong);
        Song updatedSong = mapper.map(dtoFactory.make(s->{ s.setId(originalSong.getId()); return s; }));
        BDDAssertions.assertThat(updatedSong.getTitle()).isNotEqualTo(originalSong.getTitle());

        //when
        updatedSong = jpaDao.update(updatedSong);

        //then - update missing will create
        Song dbSong = jpaDao.findById(originalSong.getId());
        BDDAssertions.then(dbSong.getTitle()).isEqualTo(updatedSong.getTitle());
        BDDAssertions.then(dbSong.getArtist()).isEqualTo(updatedSong.getArtist());
        BDDAssertions.then(dbSong.getReleased()).isEqualTo(updatedSong.getReleased());
    }

    

    @Test
    @Transactional
    void delete_exists() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song);
        jpaDao.flush();

        //when - deleting
        jpaDao.delete(song);

        //then - no long in DB
        jpaDao.flush();
        BDDAssertions.then(jpaDao.findById(song.getId())).isNull();
    }

    @Test
    //@Transactional
    void delete_does_not_exists_removing_detached_entity_without_Reansactional_annotation() {
        //given a persisted instance
        Song song = mapper.map(dtoFactory.make());

        // when
        jpaDao.create(song);
        jpaDao.flush();

        //then - deleting
        BDDAssertions.assertThatThrownBy(()->jpaDao.delete(song)).isInstanceOf(IllegalArgumentException.class);
        
    }

    @Test
    void delete_does_not_exist() {
        //given a bad ID
        int missingId = 12345;

        //when - deleting
        jpaDao.deleteById(missingId);
    }


    @Test
    void transaction_missing() {
        //given - an instance
        Song song = mapper.map(dtoFactory.make());

        //when - persist is called without a tx, an exception is thrown
        //em.persist(song);
        BDDAssertions.assertThatThrownBy(()->em.persist(song))
                .isInstanceOf(TransactionRequiredException.class);
    }


    @Test
    void transaction_present_in_component() {
        // given - an instance
        Song song = mapper.map(dtoFactory.make());  // Entity in transient state

        // when - persist called within component transaction, no exception thrown
        jpaDao.create(song);  // Entity in managed state and component method is transactional , 
        // managed entity become detached after completing the method

        // then - 
        // findById does not require managed state, since the entity is detached  it picks the entity from DB , not from persistence context 
        BDDAssertions.then(jpaDao.findById(song.getId())).isNotNull();

    }

    @Test
    void transaction_common_needed() {
        // given a persisted instance
        Song song = mapper.map(dtoFactory.make());

        // when
        jpaDao.create(song); // song is detached at this point since transaction is committed after the method ends

        // then - 
        
        //jpaDao.delete(song);  // removing a detached entity get exception
        BDDAssertions.assertThatThrownBy(()->jpaDao.delete(song)).isInstanceOf(IllegalArgumentException.class);

    }

    // @Test
    // void transaction_common_not_needed_after_merging() {
    //     // given a persisted instance
    //     Song song = mapper.map(dtoFactory.make());
    //     jpaDao.create(song); // song is detached at this point since transaction is committed after the method ends

         
    //     em.merge(song); // re-attach the detached entity with the persistence context and become managed
    //     jpaDao.delete(song); // now no exception occurs

    //     // then - 
    //     BDDAssertions.then(jpaDao.findById(song.getId())).isNull();
        
    // }

    @Test
    @Transactional
    void transaction_common_present() {
        // given a persisted instance
        Song song = mapper.map(dtoFactory.make());
        jpaDao.create(song); 

        // when 
        jpaDao.delete(song);

        // then 
        BDDAssertions.then(jpaDao.findById(song.getId())).isNull();

    }
}
