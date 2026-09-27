package com.shubham.simple_crud.Controller;

import com.shubham.simple_crud.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private ProductService service;

    @Autowired //setter injection
    public void setService(ProductService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public String getProducts(){
        return service.getProducts();
    }
}
