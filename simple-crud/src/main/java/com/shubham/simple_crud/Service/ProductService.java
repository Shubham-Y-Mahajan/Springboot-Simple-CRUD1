package com.shubham.simple_crud.Service;

import com.shubham.simple_crud.Repository.Entities.Product;

import java.util.List;

public interface ProductService {
    public List<Product> getProducts();
    public Product getProductById(String id);
    public void insertProduct(Product product);
    public void deleteProductById(String Id);

}
