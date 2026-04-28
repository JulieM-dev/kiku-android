package com.mongault.kiku.ui.cardeditor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mongault.kiku.databinding.ActivityCardEditorBinding;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.FormalityLevel;
import com.mongault.kiku.model.ReviewMode;
import com.mongault.kiku.ui.deckdetail.DeckDetailViewModel;
import com.mongault.kiku.ui.reviewer.ReviewerActivity;

public class CardEditorActivity extends AppCompatActivity {

    private ActivityCardEditorBinding binding;
    private CardEditorViewModel viewModel;
    private long deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deckId = getIntent().getLongExtra("deckId", -1);
        String deckName = getIntent().getStringExtra("deckName");
        setTitle(deckName);

        setupViewModel();
        clearFields();
        setupButtons();
        setupSpinner();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CardEditorViewModel.class);

        viewModel.getDeck().observe(this, deck -> {
            binding.textDeckName.setText(deck.getName());
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });

        viewModel.loadDeck(deckId);
    }

    private void clearFields() {
        binding.textInputJapanese.setText("");
        binding.textInputTranslated.setText("");
        binding.textInputKana.setText("");
        binding.textInputRomaji.setText("");
    }

    private FormalityLevel getFormality() {
        switch (binding.spinnerFormalityLevel.getSelectedItem().toString()) {
            case "Polite":
                return FormalityLevel.POLITE;
            case "Casual":
                return FormalityLevel.CASUAL;
        }
        return FormalityLevel.UNSPECIFIED;
    }

    private void setupButtons() {
        binding.buttonVoiceTest.setOnClickListener(v ->
                startReview(ReviewMode.PRONUNCIATION));

        binding.buttonExit.setOnClickListener(v -> finish());

        binding.buttonSaveCard.setOnClickListener(v ->
               createCard() );
    }

    private void setupSpinner() {
        binding.spinnerFormalityLevel.setSelection(0);
    }

    private void startReview(ReviewMode mode) {
        Intent intent = new Intent(this, ReviewerActivity.class);
        intent.putExtra("deckId", deckId);
        intent.putExtra("mode", mode.name());
        startActivity(intent);
    }

    private void createCard() {
        Card newCard = new Card();
        newCard.setJapanese(binding.textInputJapanese.getText().toString());
        newCard.setTranslation(binding.textInputTranslated.getText().toString());
        newCard.setKana(binding.textInputKana.getText().toString());
        newCard.setRomaji(binding.textInputRomaji.getText().toString());
        newCard.setFormalityLevel(getFormality());
        viewModel.submitNewCard(newCard);
        clearFields();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
