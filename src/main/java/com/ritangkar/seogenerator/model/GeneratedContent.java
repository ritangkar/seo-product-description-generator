package com.ritangkar.seogenerator.model;

import java.util.List;

public record GeneratedContent(
        String seoTitle,
        String metaTitle,
        String metaDescription,
        String shortDescription,
        String longDescription,
        List<String> featureBullets,
        List<String> suggestedKeywords,
        String urlSlug,
        List<FaqItem> faqItems
) {
}
