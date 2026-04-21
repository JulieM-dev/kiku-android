package com.mongault.kiku.ui.reviewer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.exoplayer.ExoPlayer;

import com.mongault.kiku.databinding.ActivityReviewBinding;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.ReviewMode;

public class ReviewerActivity extends AppCompatActivity {

    private ActivityReviewBinding binding;
    private ReviewerViewModel viewModel;
    private ExoPlayer player;
    private ReviewMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mode = ReviewMode.valueOf(getIntent().getStringExtra("mode"));
        long deckId = getIntent().getLongExtra("deckId", -1);
        setTitle(getModeTitle());

        setupPlayer();
        setupViewModel(deckId);
        setupButtons();
    }

    private String getModeTitle() {
        switch (mode) {
            case COMPREHENSION: return "Oral Comprehension";
            case PRONUNCIATION: return "Pronunciation";
            case EXPRESSION: return "Expression";
            default: return "Unknown ReviewMode";
        }
    }

    private void setupPlayer() {
        player = new ExoPlayer.Builder(this).build();
    }

    private void setupViewModel(long deckId) {
        viewModel = new ViewModelProvider(this).get(ReviewerViewModel.class);

        viewModel.getReviewedCard().observe(this, this::displayCard);

        viewModel.getIsRevealed().observe(this, isRevealed -> {
            binding.layoutAnswer.setVisibility(isRevealed ? View.VISIBLE : View.GONE);
            binding.buttonReveal.setVisibility(isRevealed ? View.GONE : View.VISIBLE);
            binding.layoutRatingButtons.setVisibility(isRevealed ? View.VISIBLE : View.GONE);

            if (isRevealed && mode.equals(ReviewMode.COMPREHENSION)) {
                playAudio();
            }
        });

        viewModel.getIsSessionFinished().observe(this, isFinished -> {
            if (isFinished) {
                Toast.makeText(this, "Session complete!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getError().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show());

        viewModel.loadDueCards(deckId, mode);
    }

    private void displayCard(Card card) {
        binding.textProgress.setText((viewModel.getCardsCount() + " cards remaining"));

        switch (mode) {
            case COMPREHENSION:
                binding.textJapanese.setVisibility(View.GONE);
                binding.buttonPlayAudio.setVisibility(View.VISIBLE);
                binding.layoutSpeed.setVisibility(View.VISIBLE);
                playAudio();
                break;

            case PRONUNCIATION:
                binding.textJapanese.setText(card.getJapanese());
                binding.textJapanese.setVisibility(View.VISIBLE);
                binding.buttonPlayAudio.setVisibility(View.GONE);
                binding.layoutSpeed.setVisibility(View.GONE);
                break;

            case EXPRESSION:
                binding.textJapanese.setText(card.getTranslation());
                binding.textJapanese.setVisibility(View.VISIBLE);
                binding.buttonPlayAudio.setVisibility(View.GONE);
                binding.layoutSpeed.setVisibility(View.GONE);
                break;
        }

        // Always populate answer fields (hidden until revealed)
        binding.textRomaji.setText(card.getRomaji());
        binding.textTranslation.setText(card.getTranslation());
    }

    private void playAudio() {
        // TODO: replace with real audio URL when backend endpoint is ready
        // "http://10.0.2.2:8080/api/audio/" + currentCard.getId()
        Card card = viewModel.getReviewedCard().getValue();
        if (card == null) return;

        String audioUrl = "http://192.168.1.77:8080/api/audio/" + card.getId();
        player.setMediaItem(MediaItem.fromUri(audioUrl));
        player.prepare();
        player.play();

    }

    private void setupButtons() {
        binding.buttonPlayAudio.setOnClickListener(v -> playAudio());

        binding.buttonReveal.setOnClickListener(v -> viewModel.revealAnswer());

        binding.buttonAgain.setOnClickListener(v -> viewModel.submitAnswer(0));
        binding.buttonHard.setOnClickListener(v -> viewModel.submitAnswer(2));
        binding.buttonGood.setOnClickListener(v -> viewModel.submitAnswer(4));
        binding.buttonEasy.setOnClickListener(v -> viewModel.submitAnswer(5));

        binding.buttonSpeed075.setOnClickListener(v -> setPlaybackSpeed(0.75f));
        binding.buttonSpeed1.setOnClickListener(v -> setPlaybackSpeed(1.0f));
        binding.buttonSpeed125.setOnClickListener(v -> setPlaybackSpeed(1.25f));
    }

    private void setPlaybackSpeed(float speed) {
        player.setPlaybackParameters(new PlaybackParameters(speed));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;
        binding = null;
    }
}