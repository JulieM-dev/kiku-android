package com.mongault.kiku.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardReview {

    private Long id;

    private Card card;

    private ReviewMode mode;

    @Builder.Default
    private int quality = 0;          // 0-5

    @Builder.Default
    private double easeFactor = 2.5;

    @Builder.Default
    private int interval = 1;

}