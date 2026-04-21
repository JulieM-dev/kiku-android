package com.mongault.kiku.data.repository;

import com.mongault.kiku.data.remote.ApiClient;
import com.mongault.kiku.data.remote.ApiService;
import com.mongault.kiku.model.Deck;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeckRepository {

    private final ApiService apiService;

    public DeckRepository() {
        this.apiService = ApiClient.getInstance();
    }

    public void getDecks(RepositoryCallback<List<Deck>> callback) {
        apiService.getDecks().enqueue(new Callback<List<Deck>>() {
            @Override
            public void onResponse(Call<List<Deck>> call, Response<List<Deck>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Deck>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getDeck(long id, RepositoryCallback<Deck> callback) {
        apiService.getDeck(id).enqueue(new Callback<Deck>() {
            @Override
            public void onResponse(Call<Deck> call, Response<Deck> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Deck> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}