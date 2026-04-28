package com.mongault.kiku.ui.cardslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mongault.kiku.R;
import com.mongault.kiku.model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(Card card);
    }

    private List<Card> cards = new ArrayList<>();
    private final OnCardClickListener listener;

    public CardsListAdapter(OnCardClickListener listener) {
        this.listener = listener;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        private final TextView textJapanese;
        private final TextView textTranslated;
        private final TextView textKanas;
        private final TextView textRomaji;
        private final TextView textFormality;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            textJapanese = itemView.findViewById(R.id.textJapanese);
            textTranslated = itemView.findViewById(R.id.textTranslated);
            textKanas = itemView.findViewById(R.id.textKanas);
            textRomaji = itemView.findViewById(R.id.textRomaji);
            textFormality = itemView.findViewById(R.id.textFormalityLevel);
        }

        public void bind(Card card) {
            textJapanese.setText(card.getJapanese());
            textTranslated.setText(card.getTranslation());
            textKanas.setText(card.getKana());
            itemView.setOnClickListener(v -> listener.onCardClick(card));
        }
    }
}