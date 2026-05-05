package com.mongault.kiku.data.remote;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.data.remote.AuthInterceptor;

// factory for ApiService
public class ApiClient {

    private static final String BASE_URL = "http://88.184.201.146:18080/";
    private static ApiService instance;

    public static ApiService getInstance(Context context) {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            AuthInterceptor authInterceptor = new AuthInterceptor(
                    TokenManager.getInstance(context)
            );

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }

    public static String getAudioUrl(String japaneseText) {
        String url = BASE_URL + "api/audio/text/" + Uri.encode(japaneseText);
        Log.d("ApiClient", "Audio URL: " + url);
        return url;
    }
}