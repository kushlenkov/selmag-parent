package kysh.corn.feedback.controller;

import kysh.corn.feedback.entity.ProductReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductReviewsRestControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("8df83bb0-4f39-4c1d-a200-dd2d4d992228"),
                        1, 1, "Отзыв №1", "user-1"),
                new ProductReview(UUID.fromString("b223df53-4eee-4e7b-beef-f1ab17756d2f"),
                        1, 3, "Отзыв №2", "user-2"),
                new ProductReview(UUID.fromString("c854a79e-728d-44f1-adc1-6a9f01ace4b6"),
                        1, 1, "Отзыв №3", "user-3")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        // when

        // then
        this.webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {
                            "id": "8df83bb0-4f39-4c1d-a200-dd2d4d992228",
                            "productId": 1,
                            "rating": 1,
                            "review": "Отзыв №1",
                            "userId": "user-1"
                            },
                            {
                            "id": "b223df53-4eee-4e7b-beef-f1ab17756d2f",
                            "productId": 1,
                            "rating": 3,
                            "review": "Отзыв №2",
                            "userId": "user-2"
                            },
                            {
                            "id": "c854a79e-728d-44f1-adc1-6a9f01ace4b6",
                            "productId": 1,
                            "rating": 1,
                            "review": "Отзыв №3",
                            "userId": "user-3"
                            }
                        ]""");
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        // given
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "review": "На пятерочку!"
                        }""")
                // then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "review": "На пятерочку!",
                            "userId": "user-tester"
                        }""")
                .jsonPath("$.id").exists();
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        // given
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": null,
                            "rating": -1,
                            "review": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer sagittis auctor volutpat. Nam felis urna, tincidunt eu ligula quis, pharetra sollicitudin leo. Nam convallis hendrerit nisl, elementum iaculis orci viverra tincidunt. Integer pretium ultrices sagittis. Fusce et nulla nisl. Praesent ut iaculis velit. Duis quis magna sit amet ipsum laoreet maximus. Pellentesque metus massa, vulputate nec bibendum id, ultrices eget urna. Donec molestie mauris at turpis facilisis, eu scelerisque quam posuere. Nulla ex eros, cursus sit amet felis nec, viverra ultricies felis. Suspendisse condimentum pellentesque metus, a mattis erat vulputate non. Sed imperdiet posuere convallis. Nunc odio magna, vulputate eget sollicitudin eget, dapibus dignissim risus. Quisque ac purus vehicula, dictum nulla sed, maximus diam. Sed in mattis magna. Integer vulputate magna egestas lacinia venenatis. Nullam blandit, lorem non porta elementum, metus est eleifend lectus, lobortis viverra erat nisl sit amet lacus. Etiam congue lectus sed massa ultrices luctus."
                        }""")
                // then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                            "errors": [
                                 "Товар не указан",
                                 "Оценка меньше 1",
                                 "Размер отзыва не должен превышать 1000 символов"
                            ]
                        }""");
    }

    @Test
    void findProductReviewsByProductId_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // when

        this.webTestClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}