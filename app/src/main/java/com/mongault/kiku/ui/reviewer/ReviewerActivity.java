package com.mongault.kiku.ui.reviewer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.databinding.ActivityReviewBinding;
import com.mongault.kiku.model.Card;
import com.mongault.kiku.model.ReviewMode;

import java.util.HashMap;

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

        setupPlayer(this);
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

    @OptIn(markerClass = UnstableApi.class)
    private void setupPlayer(Context context) {

        String token = TokenManager.getInstance(context).getToken();

        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setDefaultRequestProperties(
                        new HashMap<String, String>() {{
                            put("Authorization", "Bearer " + token);
                        }}
                );

        player = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .build();

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

        viewModel.getIsVoiceLoading().observe(this, isVoiceLoading -> {
            binding.buttonPlayAudio.setEnabled(!isVoiceLoading);
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

        binding.textRomaji.setText(card.getRomaji());
        binding.textTranslation.setText(card.getTranslation());
    }

    private void playAudio() {
        String url = viewModel.getAudioUrl();
        if (url == null) return;
        Log.d("reviewerActivity", "playAudioUrl: " + url);
        player.setMediaItem(MediaItem.fromUri(url));
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