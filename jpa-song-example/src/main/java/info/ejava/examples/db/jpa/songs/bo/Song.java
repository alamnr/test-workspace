package info.ejava.examples.db.jpa.songs.bo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Entity
@Table(name = "REPOSONGS_SONG")
@Getter
@Setter
@ToString
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name = "Song.findByArtistGESize", query = "select s from Song s  where length(s.artist) >= :length")
@NamedQuery(name = "Song.songCount", query = "select count(s) from Song s")
@NamedQuery(name = "Song.songs", query = "select s from Song s")
@NamedQuery(name = "Song.deleteSong", query = "delete from Song s where s.id=:id")
@SequenceGenerator(name = "REPOSONGS_SSONG_SEQUENCE", allocationSize = 50)
public class Song {

    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPOSONGS_SONG_SEQUENCE")
    @Column(name = "ID", nullable = false, insertable = true,updatable = false)
    private int id;

    @Column(name = "TITLE", length = 255, nullable = true, insertable = true, updatable = true)
    private String title;

    private String artist;

    private LocalDate released;

}
