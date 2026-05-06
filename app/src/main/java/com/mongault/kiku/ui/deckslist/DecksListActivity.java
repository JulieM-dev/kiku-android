package com.mongault.kiku.ui.deckslist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.databinding.ActivityDecksListBinding;
import com.mongault.kiku.ui.cardeditor.CardEditorActivity;
import com.mongault.kiku.ui.common.ToolbarManager;
import com.mongault.kiku.ui.deckdetail.DeckDetailActivity;
import com.mongault.kiku.ui.deckeditor.DeckEditorActivity;

public class DecksListActivity extends AppCompatActivity {

    private ActivityDecksListBinding binding;
    private DecksListViewModel viewModel;
    private DecksListAdapter adapter;
    private ToolbarManager toolbarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDecksListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("My Decks");

        toolbarManager = new ToolbarManager(this, binding.toolbar.toolbar);
        toolbarManager.setUsername(TokenManager.getInstance(this).getUsername());

        setupRecyclerView();
        setupViewModel();
        setupSwipeRefresh();
        setupButtons();
    }

    private void setupRecyclerView() {
        adapter = new DecksListAdapter(deck -> {
            Intent intent = new Intent(this, DeckDetailActivity.class);
            intent.putExtra("deckId", deck.getId());
            intent.putExtra("deckName", deck.getName());
            startActivity(intent);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DecksListViewModel.class);

        viewModel.getDecks().observe(this, decks -> {
            adapter.setDecks(decks);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private void setupButtons() {
        binding.buttonCreateDeck.setOnClickListener(v -> createDeck());
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadDecks());
    }

    private void createDeck() {
        Intent intent = new Intent(this, DeckEditorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}