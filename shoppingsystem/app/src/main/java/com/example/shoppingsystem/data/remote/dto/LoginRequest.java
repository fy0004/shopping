package com.example.shoppingsystem.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // Gson needs getters/setters or public fields
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}
