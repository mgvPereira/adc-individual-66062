package pt.unl.fct.di.adc.adc_individual_66062.model;

import com.google.cloud.datastore.*;
import java.time.Instant;

public class User {
    private String username;
    private String password;
    private String phone;
    private String address;
    private String role;
    private Instant createdAt;

    private static final KeyFactory keyFactory = DatastoreOptions.getDefaultInstance().getService()
            .newKeyFactory().setKind("User");

    public User() {}

    public User(String username, String password, String phone, String address, String role) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.createdAt = Instant.now();
    }

    public Entity toEntity() {
        Key key = keyFactory.newKey(username);
        return Entity.newBuilder(key)
                .set("password", password)
                .set("phone", phone)
                .set("address", address)
                .set("role", role)
                .set("createdAt", createdAt.toString())
                .build();
    }

    public static User fromEntity(Entity entity) {
        User user = new User();
        user.username = entity.getKey().getName();
        user.password = entity.getString("password");
        user.phone = entity.getString("phone");
        user.address = entity.getString("address");
        user.role = entity.getString("role");
        user.createdAt = Instant.parse(entity.getString("createdAt"));
        return user;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}