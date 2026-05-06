package com.mongault.kiku.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("username")
    private String username;
}