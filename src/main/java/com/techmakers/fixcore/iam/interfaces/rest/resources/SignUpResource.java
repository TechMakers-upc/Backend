package com.techmakers.fixcore.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpResource(
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(max = 120) String fullName,
        @Size(max = 120) String jobTitle,
        Long plantId,
        @NotBlank String role
) {
}
