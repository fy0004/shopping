package com.example.shoppingsystem.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 登录成功返回的用户信息（含 token）
 */
public class LoginResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("phone")
    private String phone;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("role")
    private String role;

    @SerializedName("token")
    private String token;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
