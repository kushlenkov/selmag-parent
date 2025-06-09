package kysh.corn.feedback.service;

import kysh.corn.feedback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsService {
    
    Mono<FavouriteProduct> addProductToFavourites(int productId, String userId);
    
    Mono<Void> removeProductFromFavourites(int productId, String userId);

    Mono<FavouriteProduct> findFavouriteProductByProduct(int productId, String userId);

    Flux<FavouriteProduct> findFavouriteProducts(String userId);
}
