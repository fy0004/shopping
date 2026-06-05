package com.example.shoppingsystem.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = "phone", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "password_hash")
    private String passwordHash;

    @ColumnInfo(name = "nickname")
    private String nickname;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    @ColumnInfo(name = "role")
    private String role; // "USER" or "ADMIN"

    @ColumnInfo(name = "status")
    private String status; // "ACTIVE" or "DISABLED"

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public User() {}

    public User(String phone, String passwordHash, String nickname, String role) {
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.role = role;
        this.status = "ACTIVE";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
