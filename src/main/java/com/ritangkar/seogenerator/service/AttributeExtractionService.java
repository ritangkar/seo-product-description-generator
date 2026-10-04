package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.ExtractedAttributes;
import com.ritangkar.seogenerator.model.ProductInputData;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;

@Component
public class AttributeExtractionService {

    public ExtractedAttributes extract(ProductInputData input) {
        List<String> cleanedFeatures = input.features().stream()
                .map(String::trim)
                .filter(f -> !f.isBlank())
                .distinct()
                .toList();

        String primaryFeature = cleanedFeatures.isEmpty() ? "quality construction" : cleanedFeatures.get(0);

        List<String> cleanedKeywords = new LinkedHashSet<>(input.keywords().stream()
                .map(k -> k.trim().toLowerCase())
                .filter(k -> !k.isBlank())
                .toList()).stream().toList();

        return new ExtractedAttributes(
                input.name().trim(), input.category().trim(), input.brand().trim(),
                cleanedFeatures, primaryFeature, input.specifications(),
                input.targetAudience().trim(), cleanedKeywords, input.tone()
        );
    }
}
