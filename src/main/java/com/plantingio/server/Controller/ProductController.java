package com.plantingio.server.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.Product;
import com.plantingio.server.Service.CloudinaryService;
import com.plantingio.server.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public ProductController(ProductService productService, CloudinaryService cloudinaryService) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
    }
    
    @GetMapping(path = "/get")
    public ResponseEntity<List<Product>> getProductsOfNursery(@RequestHeader("Authorization") String authorizationHeader) {
        return productService.getProductsOfNursery(authorizationHeader);
    }
    @GetMapping(path = "/get-nursery/{id}")
    public ResponseEntity getProductById (@PathVariable int id,
                                          @RequestHeader("Authorization") String authorizationHeader) {
        return productService.getProductById(id, authorizationHeader);
    }

    @GetMapping(path = "/get-all/{type}")
    public ResponseEntity getAllPlants (@PathVariable("type") String type) {
        return productService.getProductsByType(type);
    }

    @GetMapping(path = "/get/{id}")
    public ResponseEntity getProduct (@PathVariable int id) {
        return productService.getProduct(id);
    }

    @GetMapping(path = "/get-related/{type}/{id}")
    public ResponseEntity getRelatedProducts (@PathVariable String type, @PathVariable int id) {
        return productService.getRelatedProducts(type, id);
    }

    @PostMapping(path = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity addProduct (@RequestParam("product") String model,
                                      @RequestPart("files") MultipartFile[] files,
                                      @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(model, Product.class);
        return productService.addProduct(product, files, authorizationHeader);
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity updateProduct(@PathVariable int id,
                                        @RequestParam("product") String model,
                                        @RequestPart(value = "files", required = false) MultipartFile[] files,
                                        @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product productReq = objectMapper.readValue(model, Product.class);
        return productService.updateProduct(id, productReq, files, authorizationHeader);
    }

    @PutMapping(path = "/update/{id}/delete-photo")
    public ResponseEntity deleteProductPhoto(@PathVariable int id,
                                             @RequestParam("path") String path,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        return productService.deleteProductPhoto(id, path, authorizationHeader);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity deactivateProduct(@PathVariable int id,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        return productService.deactivateProduct(id, authorizationHeader);
    }

    @PutMapping("/nursery-deactivate/{id}")
    public ResponseEntity deactivateProductByNursery(@PathVariable int id,
                                                     @RequestHeader("Authorization") String authorizationHeader) {
        return productService.deactivateProductByNursery(id, authorizationHeader);
    }
}
