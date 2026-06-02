package info.ejava.examples.db.mongo.books.bo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Document(collection = "books")
@Getter
@Setter
@Builder
@With
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Book {

    @Id @Setter(AccessLevel.NONE)
    private String id;
    @Field(name = "title")
    private String title;
    private String author;
    private LocalDate published;
    
}
