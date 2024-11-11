package kysh.corn.manager.controller;

import jakarta.validation.Valid;
import kysh.corn.manager.controller.payload.NewProductPayload;
import kysh.corn.manager.entity.Product;
import kysh.corn.manager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {

    private final ProductService productService;

    @GetMapping("list")
    public String getProductsList(Model model) {

        model.addAttribute("products", this.productService.findAllProducts());

        return "catalogue/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage() {

        return "catalogue/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(@Valid NewProductPayload payload,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage).toList());

            return "catalogue/products/new_product";
        } else {
            Product product = this.productService.createProduct(payload.title(), payload.details());

            return "redirect:/catalogue/products/%d".formatted(product.getId());
        }
    }
}
