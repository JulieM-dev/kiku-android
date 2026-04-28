package com.mongault.kiku.ui.cardeditor;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mongault.kiku.data.repository.CardRepository;
import com.mongault.kiku.data.repository.DeckRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.CardReview;
import com.mongault.kiku.model.Deck;
import com.mongault.kiku.model.FormalityLevel;

import java.util.List;

public class CardEditorViewModel extends ViewModel {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    private final MutableLiveData<Deck> deck = new MutableLiveData<>();
    private final MutableLiveData<Card> card = new MutableLiveData<>();
    private final MutableLiveData<String> japaneseText = new MutableLiveData<>();
    private final MutableLiveData<String> translatedText = new MutableLiveData<>();
    private final MutableLiveData<String> kanaText = new MutableLiveData<>();
    private final MutableLiveData<String> romajiText = new MutableLiveData<>();
    private final MutableLiveData<FormalityLevel> formalityLevel = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCardSaved = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isVoiceLoading = new MutableLiveData<>();



    public CardEditorViewModel() {
        this.cardRepository = new CardRepository();
        this.deckRepository = new DeckRepository();
        this.isCardSaved.setValue(false);
    }

    public void loadDeck(long deckId) {
        isLoading.setValue(true);
        deckRepository.getDeck(deckId, new RepositoryCallback<Deck>() {
            @Override
            public void onSuccess(Deck data) {
                deck.setValue(data);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public void submitNewCard(Card card) {
        isLoading.setValue(true);
        Log.d("submitNewCard", "Submiting card : " + card.toString());
        cardRepository.createCard(deck.getValue().getId(), card, new RepositoryCallback<Card>() {
            @Override
            public void onSuccess(Card card) {
                isCardSaved.setValue(true);
                isLoading.setValue(false);
                Log.d("submit answer", "Submit answer a réussi");
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                throw new IllegalArgumentException("Submit answer error: " + errorMessage );
            }
        });
    }


    public String getAudioUrl(String textToSpeech) {
        if (textToSpeech == null) return null;
        return cardRepository.getAudioUrl(textToSpeech);
    }


    public LiveData<Card> getCard() { return card; }
    public LiveData<Deck> getDeck() { return deck; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsVoiceLoading() { return isVoiceLoading; }
    public void setIsVoiceLoading(Boolean value) { isVoiceLoading.setValue(value); }

}
