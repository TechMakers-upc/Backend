package com.techmakers.fixcore.iam.infrastructure.hashing.bcrypt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptHashingServiceTests {

    private final BCryptHashingService hashingService = new BCryptHashingService();

    @Test
    void shouldHashAndMatchPassword() {
        var hash = hashingService.encode("fixcore-2026");

        assertThat(hash).isNotEqualTo("fixcore-2026");
        assertThat(hashingService.matches("fixcore-2026", hash)).isTrue();
        assertThat(hashingService.matches("wrong-password", hash)).isFalse();
    }
}
