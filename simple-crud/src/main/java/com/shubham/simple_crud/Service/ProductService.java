package com.shubham.simple_crud.Service;

import com.shubham.simple_crud.Repository.Entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    public List<Product> getProducts();
    public Optional<Product> getProductById(String id);
    public void insertProduct(Product product);
    public void deleteProductById(String Id);

}
