package com.techmakers.fixcore.iam.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity(name = "User")
public class UserEntity extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 120)
    private String jobTitle;

    private Long plantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Roles role;

    protected UserEntity() {
    }

    public UserEntity(String email, String passwordHash, String fullName, String jobTitle, Long plantId, Roles role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.plantId = plantId;
        this.role = role;
    }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public String getFullName() { return fullName; }

    public String getJobTitle() { return jobTitle; }

    public Long getPlantId() { return plantId; }

    public Roles getRole() { return role; }
}
