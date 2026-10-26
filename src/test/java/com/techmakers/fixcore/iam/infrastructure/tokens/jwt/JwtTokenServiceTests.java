package com.techmakers.fixcore.iam.infrastructure.tokens.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTests {

    private final JwtTokenService tokenService =
            new JwtTokenService("fixcore-test-secret-key-with-at-least-32-bytes", 1);

    @Test
    void shouldGenerateTokenWithSubject() {
        var token = tokenService.generateToken("ana.rojas@fixcore.com");

        assertThat(tokenService.validateToken(token)).isTrue();
        assertThat(tokenService.getSubjectFromToken(token)).isEqualTo("ana.rojas@fixcore.com");
    }

    @Test
    void shouldRejectTamperedOrForeignTokens() {
        var foreign = new JwtTokenService("another-secret-key-that-is-also-32-bytes-long", 1);
        var token = foreign.generateToken("intruder@fixcore.com");

        assertThat(tokenService.validateToken(token)).isFalse();
        assertThat(tokenService.validateToken("not-a-token")).isFalse();
    }
}
