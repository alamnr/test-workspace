package info.ejava.examples.db.jpa.songs.bo;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "REPOSONGS_SONG")
@SequenceGenerator(name = "REPOSONGS_SONG_SEQUENCE", allocationSize = 50)
public class Song {

    @Id
    @GeneratedValue(generator = "REPOSONGS_SONG_SEQUENCE", strategy = GenerationType.SEQUENCE)
    private int id;
    private String title;
    private String artist;
    private LocalDate released;
    
}
