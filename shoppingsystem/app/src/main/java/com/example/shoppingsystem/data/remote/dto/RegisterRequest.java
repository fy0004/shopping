package com.example.shoppingsystem.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("nickname")
    private String nickname;

    public RegisterRequest(String phone, String password, String nickname) {
        this.phone = phone;
        this.password = password;
        this.nickname = nickname;
    }
}
