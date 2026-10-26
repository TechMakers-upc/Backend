package com.techmakers.fixcore.assetmanagement.domain.model.aggregates;

import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.Manual;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Asset {

    private final Long id;
    private final Long plantId;
    private AssetDetails details;
    private AssetStatus status;
    private Instant statusChangedAt;
    private final List<Manual> manuals;

    public Asset(Long plantId, AssetDetails details, Instant registeredAt) {
        this(null, plantId, details, AssetStatus.OPERATIONAL, registeredAt, List.of());
    }

    public Asset(Long id, Long plantId, AssetDetails details, AssetStatus status,
                 Instant statusChangedAt, List<Manual> manuals) {
        if (plantId == null) throw new IllegalArgumentException("An asset must belong to a plant");
        if (details == null) throw new IllegalArgumentException("Asset details are required");
        if (status == null || statusChangedAt == null) throw new IllegalArgumentException("Asset status is required");
        this.id = id;
        this.plantId = plantId;
        this.details = details;
        this.status = status;
        this.statusChangedAt = statusChangedAt;
        this.manuals = new ArrayList<>(manuals == null ? List.of() : manuals);
    }

    public void updateDetails(AssetDetails details) {
        if (details == null) throw new IllegalArgumentException("Asset details are required");
        this.details = details;
    }

    public void markDown(Instant at) {
        changeStatus(AssetStatus.DOWN, at);
    }

    public void markInMaintenance(Instant at) {
        changeStatus(AssetStatus.MAINTENANCE, at);
    }

    public void markOperational(Instant at) {
        changeStatus(AssetStatus.OPERATIONAL, at);
    }

    public void changeStatus(AssetStatus newStatus, Instant at) {
        if (newStatus == null || at == null) throw new IllegalArgumentException("Status and date are required");
        if (status == newStatus) return;
        status = newStatus;
        statusChangedAt = at;
    }

    public void addManual(String title, String url, Instant at) {
        manuals.add(new Manual(title, url, at));
    }

    public void removeManual(int index) {
        if (index < 0 || index >= manuals.size()) {
            throw new ResourceNotFoundException("Manual", index);
        }
        manuals.remove(index);
    }

    public Long getId() { return id; }

    public Long getPlantId() { return plantId; }

    public AssetDetails getDetails() { return details; }

    public AssetStatus getStatus() { return status; }

    public Instant getStatusChangedAt() { return statusChangedAt; }

    public List<Manual> getManuals() { return List.copyOf(manuals); }
}
