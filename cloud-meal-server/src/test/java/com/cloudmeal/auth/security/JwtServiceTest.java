package com.cloudmeal.auth.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    @Test
    void createsAndParsesToken() {
        JwtService service = new JwtService("test-secret-key-with-more-than-thirty-two-characters", 60);
        String token = service.create(7L, "tester", "USER");
        var claims = service.parse(token);
        assertThat(claims.getSubject()).isEqualTo("tester");
        assertThat(((Number) claims.get("uid")).longValue()).isEqualTo(7L);
        assertThat(claims.get("role")).isEqualTo("USER");
    }
}
