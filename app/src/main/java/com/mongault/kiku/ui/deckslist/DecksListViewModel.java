package com.mongault.kiku.ui.deckslist;

import android.app.Application;

import com.mongault.kiku.data.repository.DeckRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.Deck;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.List;

public class DecksListViewModel extends AndroidViewModel {

    private final DeckRepository deckRepository;

    private final MutableLiveData<List<Deck>> decks = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public DecksListViewModel(Application application) {
        super(application);
        this.deckRepository = new DeckRepository(application);
        loadDecks();
    }

    public void loadDecks() {
        isLoading.setValue(true);
        deckRepository.getDecks(new RepositoryCallback<List<Deck>>() {
            @Override
            public void onSuccess(List<Deck> data) {
                decks.setValue(data);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public LiveData<List<Deck>> getDecks() { return decks; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}