package kysh.corn.customer.controller;

import kysh.corn.customer.client.ProductsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsClient productsClient;

    @GetMapping("list")
    public Mono<String> getProductsListPage(Model model,
                                            @RequestParam(name = "filter", required = false) String filter) {

        model.addAttribute("filter", filter);

        return this.productsClient.findAllProducts(filter)
                .collectList()
                .doOnNext(product -> model.addAttribute("products", product))
                .thenReturn("customer/products/list");
    }
}