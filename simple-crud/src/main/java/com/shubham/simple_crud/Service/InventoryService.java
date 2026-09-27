package com.shubham.simple_crud.Service;

import com.shubham.simple_crud.Repository.Entities.Product;
import com.shubham.simple_crud.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryService implements ProductService {

    private ProductRepository repo;

    @Autowired
    public void setRepo(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getProducts(){
        return repo.findAll(); // repo methods defined inside jparepository->crudrepository
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return repo.findById(id);
    }

    @Override
    public void insertProduct(Product product) {
        repo.save(product);

    }

    @Override
    public void deleteProductById(String id) {
        repo.deleteById(id);
    }

}
