package com.ritangkar.seogenerator.model;

import java.util.List;

public record SeoContentResult(
        GeneratedContent content,
        List<ValidationIssue> seoValidationIssues,
        List<ValidationIssue> factualityIssues,
        int qualityScore,
        List<String> qualityScoreBreakdown
) {
}
