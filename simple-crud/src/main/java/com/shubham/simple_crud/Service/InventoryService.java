package com.shubham.simple_crud.Service;

import com.shubham.simple_crud.Repository.Entities.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService implements ProductService {

    private Map<String, Product> inventory = new HashMap<>(Map.of("123",new Product("123","Detergent",175,3),"157",new Product("157","Shampoo",115,7)));

    public List<Product> getProducts(){
        List<Product> result = new ArrayList<>();
        // use String Builder when there is a concatenation of String in a loop
        for (String id:inventory.keySet()){
            result.add(inventory.get(id));
        }
        return result;
    }

    @Override
    public Product getProductById(String id) {
        return inventory.getOrDefault(id,new Product("DUMMY", "PRODUCT NOT FOUND", 0,0));
    }

    @Override
    public void insertProduct(Product product) {
        inventory.putIfAbsent(product.getId(),product);

    }

    @Override
    public void deleteProductById(String id) {
        inventory.remove(id);
    }

}
