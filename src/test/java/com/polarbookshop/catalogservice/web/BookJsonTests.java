package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class BookJsonTests {
    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerialize() throws Exception {
        var book = Book.of("1234567890", "Title", "Author", 9.90, "Polarsophia");
        var jsonContent = json.write(book);
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.getIsbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.getTitle());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.getAuthor());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.getPrice());
    }

    @Test
    void testDeserialize() throws Exception {
        var content = "{\"isbn\": \"1234567890\"," +
                "\"title\": \"Title\"," +
                "\"author\": \"Author\"," +
                "\"price\": 9.90," +
                "\"publisher\": \"Polarsophia\"\n}";
        assertThat(json.parse(content))
                .usingRecursiveComparison().isEqualTo(Book.of("1234567890", "Title", "Author", 9.90, "Polarsophia"));
    }
}

