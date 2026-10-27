package com.techmakers.fixcore.iam.domain.model.aggregates;

import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;

public class User {

    private Long id;
    private final EmailAddress email;
    private String passwordHash;
    private FullName fullName;
    private String jobTitle;
    private Long plantId;
    private Roles role;

    public User(EmailAddress email, String passwordHash, FullName fullName, String jobTitle, Long plantId, Roles role) {
        this(null, email, passwordHash, fullName, jobTitle, plantId, role);
    }

    public User(Long id, EmailAddress email, String passwordHash, FullName fullName, String jobTitle, Long plantId, Roles role) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.jobTitle = jobTitle == null ? "" : jobTitle.trim();
        assignRole(role, plantId);
    }

    public void assignRole(Roles role, Long plantId) {
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }
        if (role.requiresPlant() && plantId == null) {
            throw new BusinessRuleViolationException("A %s must belong to a plant".formatted(role));
        }
        this.role = role;
        this.plantId = role.requiresPlant() ? plantId : null;
    }

    public void updateProfile(FullName fullName, String jobTitle) {
        this.fullName = fullName;
        this.jobTitle = jobTitle == null ? "" : jobTitle.trim();
    }

    public void changePassword(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
        this.passwordHash = passwordHash;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new BusinessRuleViolationException("User identifier is already assigned");
        }
        this.id = id;
    }

    public Long getId() { return id; }

    public EmailAddress getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public FullName getFullName() { return fullName; }

    public String getJobTitle() { return jobTitle; }

    public Long getPlantId() { return plantId; }

    public Roles getRole() { return role; }
}
