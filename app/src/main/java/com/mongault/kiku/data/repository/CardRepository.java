package com.mongault.kiku.data.repository;

import android.util.Log;

import androidx.media3.common.MediaItem;

import com.mongault.kiku.data.remote.ApiClient;
import com.mongault.kiku.data.remote.ApiService;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.CardReview;
import com.mongault.kiku.model.ReviewMode;
import com.mongault.kiku.model.SubmitAnswer;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardRepository {

    private final ApiService apiService;

    public CardRepository() {
        this.apiService = ApiClient.getInstance();
    }

    public void getCardsByDeck(long deckId, RepositoryCallback<List<Card>> callback) {
        apiService.getCardsByDeck(deckId).enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getDueCards(long deckId, RepositoryCallback<List<Card>> callback) {
        apiService.getDueCards(deckId).enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getDueCardsByMode(long deckId, ReviewMode mode, RepositoryCallback<List<Card>> callback) {
        apiService.getDueCardsByMode(deckId, mode).enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public CardReview getCardReview(Card card, ReviewMode mode) {
        for (CardReview cardReview : card.getReviews()) {
            if (cardReview.getMode() == mode) {
                return cardReview;
            }
        }
        throw new IllegalArgumentException("ReviewCard " + mode + " for the card " + card.getId() + " is null" );
    }

    public void createCard(long deckId, Card card, RepositoryCallback<Card> callback) {
        Log.d("CardRepository", "createCard: trying on deck " + deckId + " to send : " + card.toString() );
        apiService.createCard(deckId, card).enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    Log.d("CardRepository", "createCard: success creating on " + deckId + " the card : " + card.toString() );
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void submitAnswer(long cardId, SubmitAnswer submitAnswer, RepositoryCallback<CardReview> callback) {
        apiService.submitAnswer(cardId, submitAnswer).enqueue(new Callback<CardReview>() {
            @Override
            public void onResponse(Call<CardReview> call, Response<CardReview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CardReview> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public String getAudioUrl(String japaneseText) {
        return ApiClient.getAudioUrl(japaneseText);
    }

    public void getAudioByCardId(String japaneseText, RepositoryCallback<MediaItem> callback) {
        apiService.getAudio(japaneseText).enqueue(new Callback<MediaItem>() {
            @Override
            public void onResponse(Call<MediaItem> call, Response<MediaItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MediaItem> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}