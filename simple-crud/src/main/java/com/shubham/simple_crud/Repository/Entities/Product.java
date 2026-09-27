package com.shubham.simple_crud.Repository.Entities;

public class Product {
    private String id,name;
    private int price,quantity;

    public Product() {
        //WE MUST HAVE A NO ARGS CONSTRUCTOR
        /*Conceptually, Jackson does something like:
        JSON
         ↓
        new Product()
         ↓
        setId(...)
        setName(...)
        setPrice(...)
        setQuantity(...)*/
    }



    public Product(String id, String name, int price) {
        // Error when this constructor not present and json body only contains (id, name ,price)
        // JSON parse error:
        // Cannot map `null` into type `int` (set `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to 'false' to allow)]

        this.id = id;
        this.name = name;
        this.price = price;
        quantity=99; // NOTE: quantity set as 0 by Jackson, this does not matter
    }

    public Product(String id, String name, int price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
