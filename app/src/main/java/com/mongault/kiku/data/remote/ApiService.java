package com.mongault.kiku.data.remote;

import androidx.media3.common.MediaItem;

import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.CardReview;
import com.mongault.kiku.model.Deck;
import com.mongault.kiku.model.ReviewMode;
import com.mongault.kiku.model.SubmitAnswer;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //----------------------------------------------------------------------------------------------
    // Decks

    @GET("api/decks")
    Call<List<Deck>> getDecks();

    @GET("api/decks/{id}")
    Call<Deck> getDeck(@Path("id") long id);

    //----------------------------------------------------------------------------------------------
    // Cards

    @GET("api/cards/deck/{deckId}")
    Call<List<Card>> getCardsByDeck(@Path("deckId") long deckId);

    @GET("api/cards/deck/{deckId}/due")
    Call<List<Card>> getDueCards(@Path("deckId") long deckId);

    @GET("api/cards/deck/{deckId}/duebymode")
    Call<List<Card>> getDueCardsByMode(@Path("deckId") long deckId, @Query("mode") ReviewMode mode);

    @GET("api/cards/{id}")
    Call<Card> getCard(@Path("id") long id);

    @POST("api/cards/create/{deckId}")
    Call<Card> createCard(@Path("deckId") long deckId, @Body Card card);

    @POST("api/cards/edit/{deckId}")
    Call<Card> editCard(@Path("deckId")long deckId, @Body Card card);

    //----------------------------------------------------------------------------------------------
    // Submit a review answer

    @POST("api/reviews/card/{cardId}/answer")
    Call<CardReview> submitAnswer(@Path("cardId") long cardId, @Body SubmitAnswer submitAnswer);

    //----------------------------------------------------------------------------------------------
    // Audio

    @GET("api/audio")
    Call<MediaItem> getAudio(@Query("japaneseText") String japaneseText);

    @GET("api/audio/{cardID}")
    Call<MediaItem> getAudioByDeckId(@Path("cardId") long cardId);
}