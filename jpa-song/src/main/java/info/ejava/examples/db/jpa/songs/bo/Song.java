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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "REPOSONGS_SONG")
@NamedQuery(name="Song.deleteSong",
        query="delete from Song s where s.id=:id")

@SequenceGenerator(name = "REPOSONGS_SONG_SEQUENCE", allocationSize = 50)
public class Song {

    @Id
    @GeneratedValue(generator = "REPOSONGS_SONG_SEQUENCE", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private int id;
    @Column(name = "TITLE",length = 256, nullable = true, insertable = true, updatable = true)
    @Setter
    private String title;
    @Setter
    private String artist;
    private LocalDate released;
    
}
