package com.example.demo.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    
    // private String publicKeyPath;
    // private String privateKeyPath;
    // private String issuer;
    // private String audience;
    // private long validityInMilliseconds;

    // @Value("${PUBLIC_KEY}")
    // private String publicKey;

    // @Value("${PRIVATE_KEY}")
    // private String privateKey;

    // public JwtTokenProvider(
    //         @Value("${JWT_PUBLIC_KEY_PATH}") String publicKeyPath,
    //         @Value("${JWT_PRIVATE_KEY_PATH}") String privateKeyPath,
    //         @Value("${JWT_ISSUER}") String issuer,
    //         @Value("${JWT_AUDIENCE}") String audience,
    //         @Value("${JWT_VALIDITY_IN_MILLISECONDS}") long validityInMilliseconds) {
    //     this.publicKeyPath = publicKeyPath;
    //     this.privateKeyPath = privateKeyPath;
    //     this.issuer = issuer;
    //     this.audience = audience;
    //     this.validityInMilliseconds = validityInMilliseconds;
    // }

    public UUID retrieveIdFromToken(String token) {
        //TODO
        return UUID.fromString(token.split("\\.")[1]);
    }
}
