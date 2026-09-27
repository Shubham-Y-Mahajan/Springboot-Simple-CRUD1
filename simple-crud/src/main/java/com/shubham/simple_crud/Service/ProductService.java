package com.shubham.simple_crud.Service;

import com.shubham.simple_crud.Repository.Entities.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private Map<String, Product> inventory = new HashMap<>(Map.of("123",new Product("123","Detergent",175,3),"157",new Product("157","Shampoo",115,7)));

    public String getProducts(){
        StringBuilder result = new StringBuilder();
        // use String Builder when there is a concatenation of String in a loop
        for (String id:inventory.keySet()){
            result.append(inventory.get(id).toString());
        }
        return result.toString();
    }
}
