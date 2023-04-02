package com.polarbookshop.catalogservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {

    @Id
    Long id;
    @NotNull(message = "The book ISBN must be defined")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
    String isbn;
    @NotNull(message = "The book title must be defined")
    String title;
    @NotNull(message = "The book author must be defined")
    String author;
    @NotNull(message = "The book price must be defined")
    @Positive(message = "The book price must be greater than zero")
    Double price;
    String publisher;
    @CreatedDate
    Instant createdDate;
    @LastModifiedDate
    Instant lastModifiedDate;
    @Version
    int version;
    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;


    public static Book of(String isbn, String title, String author, Double price, String publisher, String createdBy, String lastModifiedBy) {
        return new Book(null, isbn, title, author, price, publisher, null, null, 0, createdBy, lastModifiedBy);
    }

}
