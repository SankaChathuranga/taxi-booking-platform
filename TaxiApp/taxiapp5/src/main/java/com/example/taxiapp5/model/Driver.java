package com.example.taxiapp5.model;

public class Driver {
    private Long id;
    private String name;
    private double rating;

    public Driver() {}

    public Driver(Long id, String name, double rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                '}';
    }
}