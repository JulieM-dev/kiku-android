package com.mongault.kiku.ui.deckdetail;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mongault.kiku.data.repository.CardRepository;
import com.mongault.kiku.data.repository.DeckRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.Deck;

import java.util.List;

public class DeckDetailViewModel extends AndroidViewModel {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    private final MutableLiveData<Deck> deck = new MutableLiveData<>();
    private final MutableLiveData<List<Card>> cards = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public DeckDetailViewModel(Application application) {
        super(application);
        this.cardRepository = new CardRepository(application);
        this.deckRepository = new DeckRepository(application);
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

    public void loadDueCards(Deck deck) {
        isLoading.setValue(true);
        cardRepository.getDueCards(deck.getId(), new RepositoryCallback<List<Card>>() {
            @Override
            public void onSuccess(List<Card> data) {
                cards.setValue(data);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }



    public LiveData<Deck> getDeck() { return deck; }
    public LiveData<List<Card>> getCards() { return cards; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}
