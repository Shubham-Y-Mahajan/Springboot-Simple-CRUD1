package com.shubham.simple_crud.Controller;

import com.shubham.simple_crud.Repository.Entities.Product;
import com.shubham.simple_crud.Service.InventoryService;
import com.shubham.simple_crud.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    private ProductService service;

    @Autowired //setter injection
    public void setService(ProductService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public String getProducts(){
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
