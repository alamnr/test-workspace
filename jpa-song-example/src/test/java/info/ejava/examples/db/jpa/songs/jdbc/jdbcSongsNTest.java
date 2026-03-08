package info.ejava.examples.db.jpa.songs.jdbc;

import java.sql.SQLException;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.dao.JdbcSongDAO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = NTestConfiguration.class)
//@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class jdbcSongsNTest {
    
    @Autowired
    private JdbcSongDAO jdbcSongDAO;

    @Test
    void test() throws SQLException{
        BDDAssertions.then(jdbcSongDAO).isNotNull();
        
    }
}
