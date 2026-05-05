package com.mongault.kiku.ui.deckdetail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.mongault.kiku.databinding.ActivityDeckDetailBinding;
import com.mongault.kiku.model.ReviewMode;
import com.mongault.kiku.ui.cardeditor.CardEditorActivity;
import com.mongault.kiku.ui.cardslist.CardsListActivity;
import com.mongault.kiku.ui.reviewer.ReviewerActivity;

public class DeckDetailActivity extends AppCompatActivity {

    private ActivityDeckDetailBinding binding;
    private DeckDetailViewModel viewModel;
    private long deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeckDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deckId = getIntent().getLongExtra("deckId", -1);
        String deckName = getIntent().getStringExtra("deckName");
        setTitle(deckName);

        setupViewModel();
        setupButtons();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DeckDetailViewModel.class);

        viewModel.getDeck().observe(this, deck -> {
            binding.textDeckName.setText(deck.getName());
            binding.textDeckDescription.setText(deck.getDescription());
            binding.textCardCount.setText(deck.getCards().size() + " cards");
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });

        viewModel.loadDeck(deckId);
    }

    private void setupButtons() {
        binding.buttonOralComprehension.setOnClickListener(v ->
                startReview(ReviewMode.COMPREHENSION));

        binding.buttonPronunciation.setOnClickListener(v ->
                startReview(ReviewMode.PRONUNCIATION));

        binding.buttonExpression.setOnClickListener(v ->
                startReview(ReviewMode.EXPRESSION));

        binding.buttonCreateCard.setOnClickListener(v ->
                createCard());

        binding.buttonEditCards.setOnClickListener(v -> cardsList());
    }

    private void startReview(ReviewMode mode) {
        Intent intent = new Intent(this, ReviewerActivity.class);
        intent.putExtra("deckId", deckId);
        intent.putExtra("mode", mode.name());
        startActivity(intent);
    }

    private void cardsList() {
        Intent intent = new Intent(this, CardsListActivity.class);
        intent.putExtra("deckId", deckId);
        startActivity(intent);
    }

    private void createCard() {
        Intent intent = new Intent(this, CardEditorActivity.class);
        intent.putExtra("deckId", deckId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}