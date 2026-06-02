package info.ejava.examples.db.mongo.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class BooksConfiguration {

    @Autowired
    private ConfigurableEnvironment env;
    
    
}
