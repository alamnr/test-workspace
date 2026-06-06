package info.ejava.examples.db.mongo.books.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import info.ejava.examples.db.mongo.books.bo.Book;

public interface BooksRepository extends MongoRepository<Book,String>, BookRepositoryCustom {

    List<Book> findByTitleStartingWith(String title, Sort sort);
    Slice<Book> findByTitleStartingWith(String title, Pageable pageable);
    Page<Book> findPageByTitleStartingWith(String titlePrefix, Pageable pageable);
    Optional<Book> getByTitle(String title);
    List<Book> findByTitle(String title);
    List<Book> findByTitleNot(String title);
    List<Book> findByTitleContaining(String substring);
    List<Book> findByTitleNotContaining(String substring);
    List<Book> findByTitleMatches(String regexPattern);
    List<Book> findByPublishedAfter(LocalDate published);
    List<Book> findByPublishedGreaterThanEqual(LocalDate published);
    List<Book> findByTitleNullAndPublishedAfter(LocalDate published);
    
    
    /* NotMatches would not generate a valid query -- needed to manually define */
    //    @Query("{ 'title' : { $not : { $regex: ?0} } }")
    @Query("{ 'title' : { $not : /?0/ } }")
    List<Book> findByTitleNotMatches(String regexPattern);
    /* Between was generating an exclusive search */
    @Query("{ 'published': { $gte: ?0, $lte: ?1 } }")
    List<Book> findByPublishedBetween(LocalDate starting, LocalDate ending);
    @Query(value="{ 'published': { $gte: ?0, $lte: ?1 } }", sort="{'_id':1}")
    Page<Book> findByPublishedBetween(LocalDate starting, LocalDate ending, Pageable pageable);

    //    @Query(value="{ 'title': {$exists:true}, $where: 'this.title.length >= ?0' }", fields="{'_id':0, 'title':1}")
    @Query(value="{ 'title': /^.{?0,}$/ }", fields="{'_id':0, 'title':1}")
    List<Book> getTitlesGESizeAsBook(int length);

    
}
