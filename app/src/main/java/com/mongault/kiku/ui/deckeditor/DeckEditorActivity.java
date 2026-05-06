package com.mongault.kiku.ui.deckeditor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.databinding.ActivityDeckEditorBinding;
import com.mongault.kiku.model.Deck;
import com.mongault.kiku.model.FormalityLevel;
import com.mongault.kiku.ui.deckeditor.DeckEditorViewModel;

import java.util.HashMap;

public class DeckEditorActivity extends AppCompatActivity {

    private ActivityDeckEditorBinding binding;
    private DeckEditorViewModel viewModel;
    private long deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeckEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deckId = getIntent().getLongExtra("deckId", -1);
        String deckName = getIntent().getStringExtra("deckName");
        setTitle(deckName);

        deckId = getIntent().getLongExtra("deckId", -1);


        setupViewModel();
        clearFields();
        if(deckId != -1) {
            viewModel.loadDeck(deckId);
        }
        setupPlayer(this);
        setupButtons();
        setupSpinner();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DeckEditorViewModel.class);

        viewModel.getDeck().observe(this, this::initDeck);

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });

        viewModel.getIsDeckSaved().observe(this, isDeckSaved -> {
            clearFields();
        });

    }

    private void initDeck(Deck deck) {
        Log.d("DeckEditorActivity", "initDeck: finished binding textInput, deckId : " + deck.getId());
        viewModel.setIsNewDeck(false);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupPlayer(Context context) {

        String token = TokenManager.getInstance(context).getToken();

        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setDefaultRequestProperties(
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer " + token);
                        }}
                );
    }

    private void clearFields() {
        viewModel.setIsNewDeck(true);
        binding.textInputTitle.setText("");
        binding.textInputDescription.setText("");
    }



    private void setupButtons() {

        binding.buttonExit.setOnClickListener(v -> finish());

        binding.buttonSaveDeck.setOnClickListener(v ->
               saveDeck() );
    }

    private void setupSpinner() {
        binding.spinnerDecks.setSelection(0);
    }

    private void saveDeck() {
        if(viewModel.getIsNewDeck().getValue()) {
            viewModel.validateDeck(createDeck());
        } else {
            viewModel.validateDeck(editDeck());
        }
    }

    private Deck createDeck() {
        Deck deck = new Deck();
        if(!viewModel.getIsNewDeck().getValue()) { deck = viewModel.getDeck().getValue();}
        deck.setName(binding.textInputTitle.getText().toString());
        deck.setDescription(binding.textInputDescription.getText().toString());

        return deck;
    }

    private Deck editDeck() {
        Deck deck = viewModel.getDeck().getValue();
        deck.setName(binding.textInputTitle.getText().toString());
        deck.setDescription(binding.textInputDescription.getText().toString());

        return deck;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
