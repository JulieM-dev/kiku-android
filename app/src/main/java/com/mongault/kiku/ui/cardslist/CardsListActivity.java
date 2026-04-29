package com.mongault.kiku.ui.cardslist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mongault.kiku.databinding.ActivityCardsListBinding;
import com.mongault.kiku.ui.cardeditor.CardEditorActivity;

public class CardsListActivity extends AppCompatActivity {

    private ActivityCardsListBinding binding;
    private CardsListViewModel viewModel;
    private CardsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("My Cards");

        setupViewModel();

        viewModel.setDeckId(getIntent().getLongExtra("deckId", -1));
        viewModel.loadCards();

        setupRecyclerView();
        setupSwipeRefresh();
    }

    private void setupRecyclerView() {
        adapter = new CardsListAdapter(card -> {
            Intent intent = new Intent(this, CardEditorActivity.class);
            intent.putExtra("cardId", card.getId())
                    .putExtra("deckId", viewModel.getDeckId().getValue());
            startActivity(intent);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CardsListViewModel.class);

        viewModel.getCards().observe(this, cards -> {
            adapter.setCards(cards);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadCards());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}