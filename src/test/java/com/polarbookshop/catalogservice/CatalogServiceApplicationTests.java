package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    private static KeycloakToken bjornTokens;
    private static KeycloakToken isabelleTokens;

    @Container
    private static final KeycloakContainer keycloakContainer =
            new KeycloakContainer("quay.io/keycloak/keycloak:19.0")
                    .withRealmImportFile("test-realm-config.json");


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "realms/PolarBookshop");
    }

    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder().baseUrl(keycloakContainer.getAuthServerUrl()
                        + "realms/PolarBookshop/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        isabelleTokens = authenticateWith("isabelle", "password", webClient);
        bjornTokens = authenticateWith("bjorn", "password", webClient);
    }

    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia", "Bjorn", "Bjorn");
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.getAccessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.getIsbn())
                            .isEqualTo(expectedBook.getIsbn());
                });
    }

    @Test
    void whenPostRequestUnauthorizedThen403() {
        var expectedBook = Book.of("1231231231", "Title", "Author",
                9.90, "Polarsophia","Bjorn", "Bjorn");
        webTestClient.post().uri("/books")
                .headers(headers -> headers.setBearerAuth(bjornTokens.getAccessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenPostRequestUnauthenticatedThen401() {
        var expectedBook = Book.of("1231231231", "Title", "Author",
                9.90, "Polarsophia","Bjorn", "Bjorn");
        webTestClient.post().uri("/books").bodyValue(expectedBook)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

}
