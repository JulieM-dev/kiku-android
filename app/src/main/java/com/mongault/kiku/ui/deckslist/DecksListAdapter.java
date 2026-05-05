package com.mongault.kiku.ui.deckslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mongault.kiku.R;
import com.mongault.kiku.model.Deck;

import java.util.ArrayList;
import java.util.List;

public class DecksListAdapter extends RecyclerView.Adapter<DecksListAdapter.DeckViewHolder> {

    public interface OnDeckClickListener {
        void onDeckClick(Deck deck);
    }

    private List<Deck> decks = new ArrayList<>();
    private final OnDeckClickListener listener;

    public DecksListAdapter(OnDeckClickListener listener) {
        this.listener = listener;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        holder.bind(decks.get(position));
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    class DeckViewHolder extends RecyclerView.ViewHolder {

        private final TextView textDeckName;
        private final TextView textDeckDescription;
        private final TextView textCardCount;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            textDeckName = itemView.findViewById(R.id.textDeckName);
            textDeckDescription = itemView.findViewById(R.id.textTitle);
            textCardCount = itemView.findViewById(R.id.textCardCount);
        }

        public void bind(Deck deck) {
            textDeckName.setText(deck.getName());
            textDeckDescription.setText(deck.getDescription());
            textCardCount.setText(deck.getCards().size() + " cards");
            itemView.setOnClickListener(v -> listener.onDeckClick(deck));
        }
    }
}