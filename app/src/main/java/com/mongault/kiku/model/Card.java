package com.mongault.kiku.model;

import java.util.ArrayList;
import java.util.List;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    private Long id;

    private String japanese;

    private String kana;

    private String romaji;

    private String translation;

    @Builder.Default
    private FormalityLevel formalityLevel = FormalityLevel.UNSPECIFIED;

    private Deck deck;

    @Builder.Default
    private List<CardReview> reviews = new ArrayList<>();
}
