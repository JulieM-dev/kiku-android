package com.mongault.kiku.data.remote;

import com.mongault.kiku.data.local.TokenManager;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();

        Request request = chain.request();

        if (token != null) {
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }

        return chain.proceed(request);
    }
}