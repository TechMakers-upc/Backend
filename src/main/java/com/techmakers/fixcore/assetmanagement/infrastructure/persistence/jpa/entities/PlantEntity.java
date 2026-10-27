package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "Plant")
public class PlantEntity extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 200)
    private String address;

    protected PlantEntity() {
    }

    public PlantEntity(String name, String city, String address) {
        this.name = name;
        this.city = city;
        this.address = address;
    }

    public String getName() { return name; }

    public String getCity() { return city; }

    public String getAddress() { return address; }
}
