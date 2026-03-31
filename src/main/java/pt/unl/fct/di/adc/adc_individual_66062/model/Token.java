package pt.unl.fct.di.adc.adc_individual_66062.model;

import com.google.cloud.datastore.*;
import java.time.Instant;
import java.util.UUID;

public class Token {
    private String tokenId;
    private String username;
    private String role;
    private long issuedAt;
    private long expiresAt;

    private static final KeyFactory keyFactory = DatastoreOptions.getDefaultInstance().getService()
            .newKeyFactory().setKind("Token");

    public Token() {}

    public Token(String username, String role) {
        this.tokenId = UUID.randomUUID().toString();
        this.username = username;
        this.role = role;
        this.issuedAt = Instant.now().getEpochSecond();
        this.expiresAt = issuedAt + 900; // 15 minutes
    }

    public boolean isExpired() {
        return Instant.now().getEpochSecond() > expiresAt;
    }

    public Entity toEntity() {
        Key key = keyFactory.newKey(tokenId);
        return Entity.newBuilder(key)
                .set("username", username)
                .set("role", role)
                .set("issuedAt", issuedAt)
                .set("expiresAt", expiresAt)
                .build();
    }

    public static Token fromEntity(Entity entity) {
        Token token = new Token();
        token.tokenId = entity.getKey().getName();
        token.username = entity.getString("username");
        token.role = entity.getString("role");
        token.issuedAt = entity.getLong("issuedAt");
        token.expiresAt = entity.getLong("expiresAt");
        return token;
    }

    // Getters and Setters
    public String getTokenId() { return tokenId; }
    public void setTokenId(String tokenId) { this.tokenId = tokenId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public long getIssuedAt() { return issuedAt; }
    public void setIssuedAt(long issuedAt) { this.issuedAt = issuedAt; }
    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
}