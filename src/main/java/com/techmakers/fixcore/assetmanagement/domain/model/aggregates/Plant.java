package com.techmakers.fixcore.assetmanagement.domain.model.aggregates;

import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;

public class Plant {

    private Long id;
    private String name;
    private String city;
    private String address;

    public Plant(String name, String city, String address) {
        this(null, name, city, address);
    }

    public Plant(Long id, String name, String city, String address) {
        this.id = id;
        updateDetails(name, city, address);
    }

    public void updateDetails(String name, String city, String address) {
        if (name == null || name.isBlank() || city == null || city.isBlank()) {
            throw new IllegalArgumentException("A plant needs a name and a city");
        }
        this.name = name.trim();
        this.city = city.trim();
        this.address = address == null ? "" : address.trim();
    }

    public String displayName() {
        return name.toLowerCase().contains(city.toLowerCase()) ? name : name + " · " + city;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new BusinessRuleViolationException("Plant identifier is already assigned");
        }
        this.id = id;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getCity() { return city; }

    public String getAddress() { return address; }
}
