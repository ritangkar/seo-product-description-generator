package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.ValidationIssue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple, fully deterministic scoring formula — never an LLM call.
 * Starts at 100 and deducts a fixed amount per issue found, weighted by
 * severity and stage: a factuality ERROR costs more than an SEO WARNING,
 * since an unfounded claim is a materially worse problem than a title
 * that's a few characters too long.
 *
 * Distinct from the separate Product SEO Score Calculator project's
 * scoring system, which scores arbitrary existing content against a
 * different, more detailed rubric — this score is specifically about
 * whether *this pipeline's own output* is safe to publish as-is.
 */
@Component
public class QualityScoreService {

    private static final int SEO_WARNING_PENALTY = 4;
    private static final int SEO_ERROR_PENALTY = 8;
    private static final int FACTUALITY_ERROR_PENALTY = 15;

    public record ScoreResult(int score, List<String> breakdown) {
    }

    public ScoreResult score(List<ValidationIssue> seoIssues, List<ValidationIssue> factualityIssues) {
        int total = 100;
        List<String> breakdown = new ArrayList<>();
        breakdown.add("Starting score: 100");

        for (ValidationIssue issue : seoIssues) {
            int penalty = issue.severity().equals("ERROR") ? SEO_ERROR_PENALTY : SEO_WARNING_PENALTY;
            total -= penalty;
            breakdown.add("-" + penalty + " (SEO " + issue.severity() + ": " + issue.field() + ")");
        }

        for (ValidationIssue issue : factualityIssues) {
            total -= FACTUALITY_ERROR_PENALTY;
            breakdown.add("-" + FACTUALITY_ERROR_PENALTY + " (Factuality " + issue.severity() + ": " + issue.field() + ")");
        }

        int finalScore = Math.max(0, Math.min(100, total));
        breakdown.add("Final score: " + finalScore);
        return new ScoreResult(finalScore, breakdown);
    }
}
