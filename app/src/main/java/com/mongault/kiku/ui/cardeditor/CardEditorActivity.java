package com.mongault.kiku.ui.cardeditor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.mongault.kiku.databinding.ActivityCardEditorBinding;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.FormalityLevel;
import com.mongault.kiku.model.ReviewMode;
import com.mongault.kiku.ui.deckdetail.DeckDetailViewModel;
import com.mongault.kiku.ui.reviewer.ReviewerActivity;

public class CardEditorActivity extends AppCompatActivity {

    private ActivityCardEditorBinding binding;
    private CardEditorViewModel viewModel;
    private ExoPlayer player;
    private long deckId;
    private long cardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deckId = getIntent().getLongExtra("deckId", -1);
        String deckName = getIntent().getStringExtra("deckName");
        setTitle(deckName);

        cardId = getIntent().getLongExtra("cardId", -1);


        setupViewModel();
        clearFields();
        if(cardId != -1) {
            viewModel.loadCard(cardId);
        }
        setupPlayer();
        setupButtons();
        setupSpinner();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CardEditorViewModel.class);

        viewModel.getDeck().observe(this, deck -> {
            binding.textDeckName.setText(deck.getName());
        });

        viewModel.getCard().observe(this, this::initCard);

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });

        viewModel.getIsVoiceLoading().observe(this, isVoiceLoading -> {
            binding.buttonVoiceTest.setEnabled(!isVoiceLoading);
        });

        viewModel.loadDeck(deckId);

    }

    private void initCard(Card card) {
        binding.textInputJapanese.setText(card.getJapanese());
        binding.textInputTranslated.setText(card.getTranslation());
        binding.textInputKana.setText(card.getKana());
        binding.textInputRomaji.setText(card.getRomaji());
        Log.d("CardEditorActivity", "initCard: finished binding textInput, cardId : " + card.getId());
        viewModel.setIsNewCard(false);
    }

    private void setupPlayer() {
        player = new ExoPlayer.Builder(this).build();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        viewModel.setIsVoiceLoading(true);
                        break;
                    case Player.STATE_READY:
                    case Player.STATE_ENDED:
                    case Player.STATE_IDLE:
                        viewModel.setIsVoiceLoading(false);
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                viewModel.setIsVoiceLoading(false);
            }
        });
    }

    private void clearFields() {
        viewModel.setIsNewCard(true);
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
        binding.buttonVoiceTest.setOnClickListener(v -> playAudio());

        binding.buttonExit.setOnClickListener(v -> finish());

        binding.buttonSaveCard.setOnClickListener(v ->
               createCard() );
    }

    private void setupSpinner() {
        binding.spinnerFormalityLevel.setSelection(0);
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

    private void playAudio() {
        String url = viewModel.getAudioUrl(binding.textInputJapanese.getText().toString());
        if (url == null) return;
        Log.d("reviewerActivity", "playAudioUrl: " + url);
        player.setMediaItem(MediaItem.fromUri(url));
        player.prepare();
        player.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;
        binding = null;
    }
}
