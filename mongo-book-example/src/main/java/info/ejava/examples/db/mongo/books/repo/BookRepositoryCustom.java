package info.ejava.examples.db.mongo.books.repo;

import java.util.List;
import java.util.Optional;

import info.ejava.examples.db.mongo.books.bo.Book;

public interface BookRepositoryCustom {

    List<Book> findByAuthorGESize(int length);

    Optional<Book> random();

    List<String> findByTitleGESizeAsString(int length);
    
    
}
