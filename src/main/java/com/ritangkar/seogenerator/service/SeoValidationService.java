package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.GeneratedContent;
import com.ritangkar.seogenerator.model.ValidationIssue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Deterministic SEO rules — no LLM involved in this stage under any
 * configuration. Every check has an explicit, named threshold.
 */
@Component
public class SeoValidationService {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    public List<ValidationIssue> validate(GeneratedContent content) {
        List<ValidationIssue> issues = new ArrayList<>();

        checkLength(issues, "seoTitle", content.seoTitle(), 30, 60);
        checkLength(issues, "metaTitle", content.metaTitle(), 10, 60);
        checkLength(issues, "metaDescription", content.metaDescription(), 120, 160);

        if (!SLUG_PATTERN.matcher(content.urlSlug()).matches()) {
            issues.add(new ValidationIssue("urlSlug", "ERROR",
                    "Slug must be lowercase letters, digits and hyphens only: \"" + content.urlSlug() + "\"."));
        }

        boolean anyKeywordInTitle = content.suggestedKeywords().stream()
                .anyMatch(k -> content.seoTitle().toLowerCase(Locale.ROOT).contains(k.toLowerCase(Locale.ROOT)));
        if (!anyKeywordInTitle && !content.suggestedKeywords().isEmpty()) {
            issues.add(new ValidationIssue("seoTitle", "WARNING",
                    "No suggested keyword appears in the SEO title."));
        }

        if (content.featureBullets().isEmpty()) {
            issues.add(new ValidationIssue("featureBullets", "WARNING", "No feature bullets were generated."));
        }

        return issues;
    }

    private void checkLength(List<ValidationIssue> issues, String field, String value, int min, int max) {
        int length = value.length();
        if (length < min) {
            issues.add(new ValidationIssue(field, "WARNING",
                    field + " is " + length + " characters, below the recommended minimum of " + min + "."));
        } else if (length > max) {
            issues.add(new ValidationIssue(field, "WARNING",
                    field + " is " + length + " characters, above the recommended maximum of " + max + "."));
        }
    }
}
