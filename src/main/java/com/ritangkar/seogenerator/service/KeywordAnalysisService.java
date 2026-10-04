package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.ExtractedAttributes;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Combines the caller-supplied keywords with terms extracted from the
 * product's own name, category, brand and features — so the suggested
 * keyword list is grounded in what the product actually is, not only
 * whatever the caller thought to type.
 */
@Component
public class KeywordAnalysisService {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
    private static final Set<String> STOPWORDS = Set.of(
            "a", "an", "the", "for", "with", "and", "or", "of", "to", "in", "on", "is", "are"
    );

    public List<String> analyze(ExtractedAttributes attributes) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>(attributes.providedKeywords());

        keywords.add(attributes.category().toLowerCase());
        keywords.add(attributes.brand().toLowerCase());
        keywords.add((attributes.brand() + " " + attributes.category()).toLowerCase());

        for (String feature : attributes.features()) {
            tokenize(feature).stream().limit(2).forEach(keywords::add);
        }

        return keywords.stream().filter(k -> !k.isBlank()).toList();
    }

    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        var matcher = TOKEN_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            String token = matcher.group();
            if (token.length() >= 3 && !STOPWORDS.contains(token)) tokens.add(token);
        }
        return tokens;
    }
}
