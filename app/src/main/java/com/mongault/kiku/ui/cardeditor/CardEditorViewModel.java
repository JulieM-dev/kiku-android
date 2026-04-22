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

import java.util.List;

public class CardEditorViewModel extends ViewModel {

    private final CardRepository cardRepository;

    private final MutableLiveData<Long> deckId = new MutableLiveData<>();
    private final MutableLiveData<Card> card = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCardSaved = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public CardEditorViewModel (Long deckId) {
        this.cardRepository = new CardRepository();
        this.deckId.setValue(deckId);
        this.isCardSaved.setValue(false);
    }

    public CardEditorViewModel() {
        this.cardRepository = new CardRepository();
        this.isCardSaved.setValue(false);
    }

    public void submitNewCard(long deckId) {
        isLoading.setValue(true);
        cardRepository.createCard(deckId, card.getValue(), new RepositoryCallback<Card>() {
            @Override
            public void onSuccess(Card card) {
                isCardSaved.setValue(true);
                Log.d("submit answer", "Submit answer a réussi");
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                throw new IllegalArgumentException("Submit answer error: " + errorMessage );
            }
        });
    }




    public LiveData<Card> getCard() { return card; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}
