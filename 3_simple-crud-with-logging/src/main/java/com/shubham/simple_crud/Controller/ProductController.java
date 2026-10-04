package com.shubham.simple_crud.Controller;

import com.shubham.simple_crud.Repository.Entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.shubham.simple_crud.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private ProductService service;

    @Autowired //setter injection
    public void setService(ProductService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public String getProducts(){
        logger.info("Oye api hit kiya hoye");
        return service.getProducts().toString();
    }

    @GetMapping("/products/{id}")
    public String getProductById(@PathVariable String id){
        return service.getProductById(id).toString();
    }

    @PostMapping("/products")
    public String addProduct(@RequestBody Product product){
        service.insertProduct(product);
        return "Product " + product.toString() + " Added";
    }

    @DeleteMapping("/products/{id}")
    public String deleteProductById(@PathVariable String id){
        service.deleteProductById(id);
        return "Deleted";
    }
}
