package kysh.corn.customer.config;

import kysh.corn.customer.client.WebClientFavouriteProductsClient;
import kysh.corn.customer.client.WebClientProductReviewsClient;
import kysh.corn.customer.client.WebClientProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl
    ) {

        return new WebClientProductsClient(WebClient.builder()
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String feedbackBaseUrl
    ) {

        return new WebClientFavouriteProductsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String feedbackBaseUrl
    ) {

        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}