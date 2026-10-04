package com.ritangkar.seogenerator.model;

import java.util.List;
import java.util.Map;

/**
 * Stage 1 output: the input, normalized and with a "primary feature"
 * identified (the first feature supplied, used as the headline benefit in
 * generated titles and short descriptions).
 */
public record ExtractedAttributes(
        String name,
        String category,
        String brand,
        List<String> features,
        String primaryFeature,
        Map<String, String> specifications,
        String targetAudience,
        List<String> providedKeywords,
        Tone tone
) {
}
