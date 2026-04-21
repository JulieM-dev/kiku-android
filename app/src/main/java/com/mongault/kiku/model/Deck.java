package com.mongault.kiku.model;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck {

    private Long id;

    private String name;

    private String description;

    @Builder.Default
    private List<Card> cards = new ArrayList<>();
}