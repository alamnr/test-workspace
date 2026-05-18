package info.ejava.examples.db.jpa.songs.dao;

import org.springframework.stereotype.Component;

import info.ejava.examples.db.jpa.songs.bo.Song;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JpaSongDAO {
    
    private final EntityManager em;

    // JPQL
    public boolean existsById(int id) {
        return em.createQuery("select count(s) from Song s where s.id=:id", Number.class)
                    .setParameter("id", id)
                    .getSingleResult()
                    .longValue()==1L;
    }

    public Song findById(int id) {
        return em.find(Song.class, id);
    }
    
    public void create(Song song) {
        em.persist(song); // song entity now in managed state 
        // with in persistence context any change in managed entity will issue a delayed future update command
        //song.setArtist("Artist updated"); 
    }

    public Song update(Song song) {
        return em.merge(song);
    }

    public void delete(Song song) {
        em.remove(song);
    }

    public void deleteById(int id) {
        em.createNamedQuery("Song.deleteSong")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void deleteAll() {
        em.createNativeQuery("delete from REPOSONGS_SONG")
            .executeUpdate();
    }

    // immediately save the persistence context state into database but not commit yet , so the change is not available to other transaction 
    // or console, available only to that current thread transacion
    public void flush() { 
        em.flush();
    }

    // Clear all the managed entities from the persistence context
    public void clear() {
        em.clear();
    }

    // clear only one managed entity from the persistence context
    public void detach(Object obj){
        em.detach(obj);
    }
}
