package com.android.adahi.models;

/**
 * Model class representing an Adahi (sacrificial animal).
 * Contains all necessary information about the animal for display and ordering.
 */
public class Animal {
    private String id;
    private String name;
    private String type; // e.g., Sheep, Goat, Cow, Buffalo
    private double price;
    private double weight; // in kg
    private String description;
    private String imageUrl;
    private int quantity; // Available quantity

    /**
     * Default constructor required for Firebase
     */
    public Animal() {
    }

    /**
     * Constructor with all parameters
     */
    public Animal(String id, String name, String type, double price, double weight, 
                  String description, String imageUrl, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.weight = weight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    // Getters and Setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                ", description='" + description + '\'' +
                '}';
    }
}
