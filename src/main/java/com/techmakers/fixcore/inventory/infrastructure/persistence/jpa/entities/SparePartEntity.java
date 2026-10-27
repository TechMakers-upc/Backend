package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity(name = "SparePart")
public class SparePartEntity extends AuditableEntity {

    @Column(nullable = false)
    private Long plantId;

    @Column(nullable = false, length = 40)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PartUnit unit;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int minStock;

    @Column(nullable = false, length = 60)
    private String location;

    protected SparePartEntity() {
    }

    public SparePartEntity(Long plantId, String sku, String name, PartCategory category, PartUnit unit,
                           int stock, int minStock, String location) {
        this.plantId = plantId;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.stock = stock;
        this.minStock = minStock;
        this.location = location;
    }

    public Long getPlantId() { return plantId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public PartCategory getCategory() { return category; }
    public PartUnit getUnit() { return unit; }
    public int getStock() { return stock; }
    public int getMinStock() { return minStock; }
    public String getLocation() { return location; }
}
