package com.ritangkar.seogenerator.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record ProductInputData(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String brand,
        @NotEmpty List<String> features,
        Map<String, String> specifications,
        String targetAudience,
        List<String> keywords,
        @NotNull Tone tone
) {
    public ProductInputData {
        if (specifications == null) specifications = Map.of();
        if (keywords == null) keywords = List.of();
        if (targetAudience == null || targetAudience.isBlank()) targetAudience = "everyday shoppers";
    }
}
