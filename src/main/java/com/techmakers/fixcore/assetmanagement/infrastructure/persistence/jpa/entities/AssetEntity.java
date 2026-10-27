package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetCategory;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.Criticality;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Asset")
public class AssetEntity extends AuditableEntity {

    @Column(nullable = false)
    private Long plantId;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Criticality criticality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus status;

    @Column(nullable = false)
    private Instant statusChangedAt;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false, length = 80)
    private String brand;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(nullable = false, length = 80)
    private String serialNumber;

    private LocalDate installedAt;

    @Column(nullable = false, length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "AssetManual", joinColumns = @JoinColumn(name = "assetId"))
    @OrderColumn(name = "position")
    private List<ManualEmbeddable> manuals = new ArrayList<>();

    protected AssetEntity() {
    }

    public AssetEntity(Long plantId, AssetStatus status, Instant statusChangedAt) {
        this.plantId = plantId;
        this.status = status;
        this.statusChangedAt = statusChangedAt;
    }

    public void setDetails(String code, String name, AssetCategory category, Criticality criticality, String location,
                           String brand, String model, String serialNumber, LocalDate installedAt, String description) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.criticality = criticality;
        this.location = location;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.installedAt = installedAt;
        this.description = description;
    }

    public void setManuals(List<ManualEmbeddable> manuals) {
        this.manuals = new ArrayList<>(manuals);
    }

    public Long getPlantId() { return plantId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public AssetCategory getCategory() { return category; }
    public Criticality getCriticality() { return criticality; }
    public AssetStatus getStatus() { return status; }
    public Instant getStatusChangedAt() { return statusChangedAt; }
    public String getLocation() { return location; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getSerialNumber() { return serialNumber; }
    public LocalDate getInstalledAt() { return installedAt; }
    public String getDescription() { return description; }
    public List<ManualEmbeddable> getManuals() { return manuals; }
}
