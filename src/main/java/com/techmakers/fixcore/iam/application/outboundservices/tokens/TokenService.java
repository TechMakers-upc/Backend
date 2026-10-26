package com.techmakers.fixcore.iam.application.outboundservices.tokens;

public interface TokenService {

    String generateToken(String subject);

    String getSubjectFromToken(String token);

    boolean validateToken(String token);
}
