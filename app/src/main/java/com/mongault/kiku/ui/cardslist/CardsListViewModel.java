package com.mongault.kiku.ui.cardslist;

import com.mongault.kiku.data.repository.CardRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.Card;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.List;

public class CardsListViewModel extends ViewModel {

    private final CardRepository cardRepository;

    private final MutableLiveData<Long> deckId = new MutableLiveData<>();
    private final MutableLiveData<List<Card>> cards = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public CardsListViewModel() {
        this.cardRepository = new CardRepository();
    }

    public void loadCards() {
        isLoading.setValue(true);
        cardRepository.getCardsByDeck(deckId.getValue(), new RepositoryCallback<List<Card>>() {
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

    public LiveData<Long> getDeckId() { return deckId; }
    public void setDeckId(Long deckId) { this.deckId.setValue(deckId);}
    public LiveData<List<Card>> getCards() { return cards; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}