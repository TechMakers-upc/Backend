package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

@Embeddable
public class ManualEmbeddable {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Instant addedAt;

    protected ManualEmbeddable() {
    }

    public ManualEmbeddable(String title, String url, Instant addedAt) {
        this.title = title;
        this.url = url;
        this.addedAt = addedAt;
    }

    public String getTitle() { return title; }

    public String getUrl() { return url; }

    public Instant getAddedAt() { return addedAt; }
}
