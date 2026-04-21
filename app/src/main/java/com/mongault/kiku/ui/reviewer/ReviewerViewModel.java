package com.mongault.kiku.ui.reviewer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mongault.kiku.data.repository.CardRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.CardReview;
import com.mongault.kiku.model.Deck;
import com.mongault.kiku.model.ReviewMode;
import com.mongault.kiku.model.SubmitAnswer;

import java.util.List;

public class ReviewerViewModel extends ViewModel {

    private final CardRepository cardRepository;

    private final MutableLiveData<Long> deckId = new MutableLiveData<>();
    private final MutableLiveData<Card> reviewedCard = new MutableLiveData<>();
    private final MutableLiveData<ReviewMode> mode = new MutableLiveData<>();
    private final MutableLiveData<List<Card>> dueCardsQueue = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRevealed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSessionFinished = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ReviewerViewModel(long deckId, ReviewMode mode) {
        this.cardRepository = new CardRepository();
        this.mode.setValue(mode);
        this.deckId.setValue(deckId);
        loadDueCards(deckId, mode);
    }

    public ReviewerViewModel() {
        this.cardRepository = new CardRepository();
    }

    public void loadDueCards(Long deckId, ReviewMode reviewMode) {
        isLoading.setValue(true);
        cardRepository.getDueCardsByMode(deckId, reviewMode, new RepositoryCallback<List<Card>>() {
            @Override
            public void onSuccess(List<Card> data) {
                dueCardsQueue.setValue(data);
                isLoading.setValue(false);
                mode.setValue(reviewMode);
                showCurrentCard();
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public void revealAnswer() {
        isRevealed.setValue(true);
    }

    public void submitAnswer(int quality) {
        Card card = reviewedCard.getValue();
        if (card == null) return;

        SubmitAnswer submitAnswer = new SubmitAnswer(mode.getValue(), quality);
        cardRepository.submitAnswer(card.getId(), submitAnswer, new RepositoryCallback<CardReview>() {
            @Override
            public void onSuccess(CardReview data) {
                moveToNextCard();
                Log.d("submit answer", "Submit answer a réussi");
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                throw new IllegalArgumentException("Submit answer error: " + errorMessage );
            }
        });
    }


    private void showCurrentCard() {
        List<Card> queue = dueCardsQueue.getValue();

        if (queue.isEmpty()) {
            isSessionFinished.setValue(true);
            return;
        }
        reviewedCard.setValue(queue.get(0));
    }

    private void moveToNextCard() {
        List<Card> queue = dueCardsQueue.getValue();

        if (queue == null || queue.isEmpty()) {
            isSessionFinished.setValue(true);
            return;
        }
        queue.remove(0);
        isRevealed.setValue(false);
        showCurrentCard();
    }

    public int getCardsCount() {
        List<Card> queue = dueCardsQueue.getValue();

        if (queue == null || queue.isEmpty()) {
            return 0;
        }
        return dueCardsQueue.getValue().size();
    }


    public LiveData<Long> getDeckId() { return deckId; }
    public LiveData<List<Card>> getDueCards() { return dueCardsQueue; }
    public LiveData<Card> getReviewedCard() { return reviewedCard; }
    public LiveData<ReviewMode> getMode() { return mode; }
    public LiveData<Boolean> getIsRevealed() { return isRevealed; };
    public LiveData<Boolean> getIsSessionFinished() { return isSessionFinished; };
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}
