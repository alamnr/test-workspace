package info.ejava.examples.db.jpa.songs.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.springframework.stereotype.Component;

import info.ejava.examples.db.jpa.songs.bo.Song;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JdbcSongDAO {

    private final DataSource dataSource;
    private Dialect dialect;

    enum Dialect {
        H2("call next value for REPOSONGS_SONG_SEQUENCE"),
        POSTGRES("select nextval('REPOSONGS_SONG_SEQUENCE')");

        private final String nextValSql;
        Dialect(String nextValSql){
            this.nextValSql = nextValSql;
        }

        String getNextValSql() {return nextValSql;}
    }

    @PostConstruct
    public void init() {
        try (Connection conn = dataSource.getConnection() ) {
            String url = dataSource.getConnection().getMetaData().getURL();
            if(url.contains("postgresql")){
                dialect = Dialect.POSTGRES;
            } else if(url.contains("h2")){
                dialect = Dialect.H2;
            } else {
                throw new IllegalStateException("unsupported dialect: " + url);
            }
            
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    
    public boolean existsById(int id) throws SQLException {
        String sql = "select count(*) from REPOSONGS_SONG where id =?";
        // log.info("{}, params={}", sql, List.of(id));
        // Connection conn = dataSource.getConnection();
        // PreparedStatement stmt = conn.prepareStatement(sql);
        // try(conn;stmt){ // alternatively 
        // }

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, id);
                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        long count = rs.getLong(1);
                        return count != 0 ;
                    }
                    throw new IllegalStateException("no result from count.");
                }
        }
    }

    public Song findById(int id) throws SQLException {
        String sql = "select title, artist, released from REPOSONGS_SONG where id = ?";
        log.info("{}, params={}", sql, List.of(id));
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, id);
                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        Date releasedDate = rs.getDate(3);
                        return Song.builder()
                                .id(id)
                                .title(rs.getString(1))
                                .artist(rs.getString(2))
                                .released(releasedDate == null ? null : releasedDate.toLocalDate())
                                .build();
                    } else {
                        throw new NoSuchElementException(String.format("Song[%d] not found", id));
                    }
                }
            }
    }

    public void create(Song song) throws SQLException {
        String sql = "insert into REPOSONGS_SONG(id,title,artist,released) values(?,?,?,?)";

        try(Connection conn  = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                int id = nextId(conn); //get next ID from database
                log.info("{}, param={}", sql, List.of(id, song.getTitle(), song.getArtist(),song.getReleased()));

                stmt.setInt(1, id);
                stmt.setString(2, song.getTitle());
                stmt.setString(3, song.getArtist());
                stmt.setDate(4, Date.valueOf(song.getReleased()));
                stmt.executeUpdate();   

                setId(song,id);
        }
    }


    private void setId(Song song, int id){
        try {
            Field f = Song.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(song,id);
        } catch (NoSuchElementException | IllegalAccessException | NoSuchFieldException | SecurityException ex ) {
            throw new IllegalStateException("unable to set Song.id", ex);
        }
    }



    private int nextId(Connection conn) throws SQLException {
        String sql = dialect.getNextValSql();
        log.info(sql);
        try(PreparedStatement call = conn.prepareStatement(sql);
            ResultSet rs = call.executeQuery()){
                if(rs.next()){
                    Long id = rs.getLong(1);
                    return id.intValue();
                } else {
                    throw new IllegalStateException("no sequence result returned from call");
                }
            }
    }

}
