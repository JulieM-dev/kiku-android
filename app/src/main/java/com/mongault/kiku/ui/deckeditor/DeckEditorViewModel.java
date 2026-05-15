package com.mongault.kiku.ui.deckeditor;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mongault.kiku.data.repository.DeckRepository;
import com.mongault.kiku.data.repository.DeckRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.Deck;
import com.mongault.kiku.model.Deck;
import com.mongault.kiku.model.FormalityLevel;

public class DeckEditorViewModel extends AndroidViewModel {

    private final DeckRepository deckRepository;

    private final MutableLiveData<Deck> deck = new MutableLiveData<>();
    private final MutableLiveData<String> japaneseText = new MutableLiveData<>();
    private final MutableLiveData<String> translatedText = new MutableLiveData<>();
    private final MutableLiveData<String> kanaText = new MutableLiveData<>();
    private final MutableLiveData<String> romajiText = new MutableLiveData<>();
    private final MutableLiveData<FormalityLevel> formalityLevel = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isNewDeck = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDeckSaved = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isVoiceLoading = new MutableLiveData<>();



    public DeckEditorViewModel(Application application) {
        super(application);
        this.deckRepository = new DeckRepository(application);
        this.isDeckSaved.setValue(false);
        this.isNewDeck.setValue(true);
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

    public void validateDeck(Deck deck) {
        if(isNewDeck.getValue()) {
            submitNewDeck(deck);
        } else {
            submitEditDeck(deck);
        }
    }
    public void submitNewDeck(Deck deck) {
        isLoading.setValue(true);
        deckRepository.createDeck(deck, new RepositoryCallback<Deck>() {
            @Override
            public void onSuccess(Deck deck) {
                isDeckSaved.setValue(true);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                throw new IllegalArgumentException("Submit answer error: " + errorMessage );
            }
        });
    }

    public void submitEditDeck(Deck deck) {
        isLoading.setValue(true);
        Log.d("submitEditDeck", "Submiting deck : " + deck.toString());
        deckRepository.editDeck(deck, new RepositoryCallback<Deck>() {
            @Override
            public void onSuccess(Deck deck) {
                isDeckSaved.setValue(true);
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






    public LiveData<Deck> getDeck() { return deck; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsDeckSaved() { return isDeckSaved; }
    public LiveData<Boolean> getIsNewDeck() { return isNewDeck ; }
    public void setIsNewDeck(Boolean value) { isNewDeck.setValue(value);}

}
