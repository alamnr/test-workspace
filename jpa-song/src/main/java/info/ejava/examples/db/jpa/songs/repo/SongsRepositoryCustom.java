package info.ejava.examples.db.jpa.songs.repo;

import java.util.Optional;

import info.ejava.examples.db.jpa.songs.bo.Song;

public interface SongsRepositoryCustom {
    Optional<Song> random();
}
