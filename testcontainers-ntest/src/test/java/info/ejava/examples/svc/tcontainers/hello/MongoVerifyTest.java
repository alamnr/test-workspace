package info.ejava.examples.svc.tcontainers.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * This is a self-test of the actual_hostport_matches_expected() method.
 * It provides the method for other tests to use and locally verifies its
 * validity.
 */
@Slf4j
public class MongoVerifyTest {
    //..., servers=[{address=localhost:56295, type=STANDALONE...
    private static final Pattern DESCR_ADDRESS_PATTERN = Pattern.compile("address=([A-Za-z\\.:0-9]+),");
    //mongodb://admin:secret@localhost:27017/testcontainers
    private static final Pattern URL_HOSTPORT_PATTERN = Pattern.compile("[@/]([A-Za-z\\.:0-9]+)/");

    @ParameterizedTest
    @CsvSource(delimiterString = "@", value={
            "{type=STANDALONE, servers=[{address=localhost:56295, type=STANDALONE, roundTripTime=20.3 ms, state=CONNECTED}]}@localhost:56295",
            "{type=STANDALONE, servers=[{address=host.docker.internal:56295, type=STANDALONE, roundTripTime=20.3 ms, state=CONNECTED}]}@host.docker.internal:56295"
    })
    void description_hostport_matches(String shortDescription, String hostPort) {
        Matcher m = DESCR_ADDRESS_PATTERN.matcher(shortDescription);
        then(shortDescription).matches(s->m.find(), DESCR_ADDRESS_PATTERN.toString());
        then(m.group(1)).isEqualTo(hostPort);
    }

    @ParameterizedTest
    @CsvSource(value={
            "spring.data.mongodb.uri=mongodb://admin:secret@localhost:56295/test?authSource=admin,localhost:56295",
            "spring.data.mongodb.uri=mongodb://admin:secret@host.docker.internal:56295/test?authSource=admin,host.docker.internal:56295",
    })
    void url_hostport_matches(String url, String hostPort) {
        Matcher m = URL_HOSTPORT_PATTERN.matcher(url);
        then(url).matches(s->m.find(),URL_HOSTPORT_PATTERN.toString());
        then(m.group(1)).isEqualTo(hostPort);
    }


    @ParameterizedTest
    @CsvSource(delimiterString = "#", value={
            "spring.data.mongodb.uri=mongodb://admin:secret@localhost:56295/test?authSource=admin,localhost:56295#" +
                    "{type=STANDALONE, servers=[{address=localhost:56295, type=STANDALONE, roundTripTime=20.3 ms, state=CONNECTED}]}@localhost:56295#",
            "spring.data.mongodb.uri=mongodb://admin:secret@host.docker.internal:56295/test?authSource=admin,host.docker.internal:56295#" +
                    "{type=STANDALONE, servers=[{address=host.docker.internal:56295, type=STANDALONE, roundTripTime=20.3 ms, state=CONNECTED}]}@host.docker.internal:56295"
    })
    void actual_hostport_matches_expected(String expectedMongoUrl, String description) {
        Matcher m1 = DESCR_ADDRESS_PATTERN.matcher(description);
        then(expectedMongoUrl).matches(url->m1.find(), DESCR_ADDRESS_PATTERN.toString());
        log.info("{} host:port reported={}", description, m1.group(1));

        Matcher m2 = URL_HOSTPORT_PATTERN.matcher(expectedMongoUrl);
        then(expectedMongoUrl).matches(url->m2.find(), URL_HOSTPORT_PATTERN.toString());
        log.info("{} host:port expected={}", expectedMongoUrl, m2.group(1));

        then(m1.group(1)).isEqualTo(m2.group(1));
    }
}
