package info.ejava.examples.db.jpa.songs.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

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

        private final String nextvalSql;
        Dialect(String nextvalSql){
            this.nextvalSql = nextvalSql;
        }

        String getNextvalSql(){
            return nextvalSql;
        }
    }

    @PostConstruct
    public void init() {
        try (Connection conn = dataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            if(url.contains("postgresql")){
                dialect = Dialect.POSTGRES;
            } else if (url.contains("h2")) {
                dialect = Dialect.H2;
            } else {
                throw new IllegalStateException("unsupported dialect: " + url);
            }
            
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public boolean existsById(int id) throws SQLException {
        String sql = "select count(*) from REPOSONGS_SONG where id = ?";
        log.info("{} , params={} ", sql, List.of(id));
        // Connection conn = dataSource.getConnection();
        // PreparedStatement statement = conn.prepareStatement(sql);
        // try (conn;statement) 
        try(Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql))
        {
          statement.setInt(1, id);  
          try (ResultSet rs = statement.executeQuery()) {
            if(rs.next()) {
                long count = rs.getLong(1);
                return count !=0;
            }
            throw new IllegalStateException("no result from count");
          } 
        } 
    }
    
    public Song findById(int id) throws SQLException {
        String sql = "select title, artist, released from REPOSONGS_SONG where id = ? ";
        log.info("{} , params={} ", sql, List.of(id));
        try(Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    Date releasedDate = rs.getDate(3);
                    return Song.builder()
                                .id(id)
                                .title(rs.getString(1))
                                .artist(rs.getString(2))
                                .released(releasedDate == null ? null : releasedDate.toLocalDate())
                                .build();
                } else {
                    throw new NoSuchElementException(String.format("song[%d] not found", id));
                }
            }
        }
        
    }

    public void create(Song song) throws SQLException {
        String sql = "insert into REPOSONGS_SONG(id,title,artist,released) values(?,?,?,?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            int id  = nextId(conn);
            log.info("{} , params = {} ", sql, List.of(id,song.getTitle(), song.getArtist(), song.getReleased()));

            statement.setInt(1, id);
            statement.setString(2, song.getTitle());
            statement.setString(3, song.getArtist());
            statement.setDate(4, Date.valueOf(song.getReleased()));
            statement.executeUpdate();

            setId(song, id) ;  // inject id in supplied song
        }
    }

    
    private int nextId(Connection conn)  throws SQLException {
        String sql = dialect.getNextvalSql();
        log.info(sql);       
        try (PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            if(rs.next()) {
                Long id = rs.getLong(1);
                return id.intValue();
            } else {
                throw new IllegalStateException("no sequence result returned from call");
            }
        } 
    }
    
    private void setId(Song song, int id) {
        try {
            Field f = Song.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(song, id);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException(" unable to set Song.id ", ex);
        }
    }

    public void update(Song song) throws SQLException {
        String sql = "update REPOSONGS_SONG set title=?, artist=?, released=? where id=?";
        log.info("{}, params={} ", sql, List.of(song.getTitle(), song.getArtist(),song.getReleased(), song.getId()));
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            LocalDate releasedDate = song.getReleased();
            statement.setString(1, song.getTitle());
            statement.setString(2, song.getArtist());
            statement.setDate(3, releasedDate==null ? null : Date.valueOf(releasedDate));
            statement.setInt(4, song.getId());
            int count = statement.executeUpdate();
            if(count!=1) {
                throw new NoSuchElementException(String.format("song[%d] not found", song.getId()));     
            }
            
        } 
    }

    public void deleteById(int id) throws SQLException {
        String sql = "delete from REPOSONGS_SONG where id=?";
        log.info("{}, params={}", sql, List.of(id));
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);  
            int count = statement.executeUpdate();
            if(count!=1) {
                throw new NoSuchElementException(String.format("song[%d] not found", id));
            }
        } 
    }

    public void deleteAll() throws SQLException {
        String sql = "delete from REPOSONGS_SONG";
        log.info("{}, params={}", sql, List.of());
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();  
        } 
    }
}