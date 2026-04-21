package com.mongault.kiku.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmitAnswer {

    private ReviewMode mode;

    private int quality;
}
