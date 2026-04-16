package info.ejava.examples.svc.docker.hello.controllers;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HelloDBController {
    private final DataSource dataSource;
    private final MongoClient mongoClient;

    @GetMapping(path="/api/hello/jdbc",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public String helloDataSource() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String jdbcUrl=conn.getMetaData().getURL();
            log.info("jdbcUrl={}", jdbcUrl);
            return jdbcUrl;
        }
    }

    @GetMapping(path="/api/hello/mongo",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public String helloMongoClient() {
        log.info("dbNames: {}", mongoClient.listDatabaseNames().first()); //test connection
        return mongoClient.getClusterDescription().getShortDescription();
    }
}
