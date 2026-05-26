package info.ejava.examples.db.jpa.songs.repo;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaContext;

import info.ejava.examples.db.jpa.songs.bo.Song;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;


public class SongsRepositoryCustomImpl implements SongsRepositoryCustom {

    private final EntityManager em;
    private final SecureRandom random = new SecureRandom();
    @Autowired @Lazy
    private SongsRepository songsRepository;

    public SongsRepositoryCustomImpl(JpaContext jpaContext) {
        em=jpaContext.getEntityManagerByManagedType(Song.class);
    }

    protected List<Song> songs(int offset, int limit) {
        return em.createNamedQuery("Song.songs")
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }



    public Optional<Song> random(){
        Optional randomSong = Optional.empty();
        int count = (int) songsRepository.count(); 
        if (count!=0) {
        int offset = random.nextInt(count);
        List<Song> songs = songs(offset, 1); 
        randomSong = songs.isEmpty() ? Optional.empty():Optional.of(songs.get(0));
        }
        return randomSong;
    }
}
