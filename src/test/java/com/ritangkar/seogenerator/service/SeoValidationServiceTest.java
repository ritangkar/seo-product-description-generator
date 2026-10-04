package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.GeneratedContent;
import com.ritangkar.seogenerator.model.ValidationIssue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SeoValidationServiceTest {

    private final SeoValidationService service = new SeoValidationService();

    private GeneratedContent contentWith(String seoTitle, String metaTitle, String metaDescription, String slug) {
        return new GeneratedContent(seoTitle, metaTitle, metaDescription, "short", "long",
                List.of("feature bullet"), List.of("keyword"), slug, List.of());
    }

    @Test
    void flagsATitleThatIsTooShort() {
        List<ValidationIssue> issues = service.validate(
                contentWith("Short", "meta", "A".repeat(140), "valid-slug"));
        assertThat(issues).anyMatch(i -> i.field().equals("seoTitle") && i.message().contains("below"));
    }

    @Test
    void flagsATitleThatIsTooLong() {
        List<ValidationIssue> issues = service.validate(
                contentWith("A".repeat(80), "meta", "A".repeat(140), "valid-slug"));
        assertThat(issues).anyMatch(i -> i.field().equals("seoTitle") && i.message().contains("above"));
    }

    @Test
    void flagsAMalformedSlug() {
        List<ValidationIssue> issues = service.validate(
                contentWith("A".repeat(45), "meta", "A".repeat(140), "Not A Valid Slug!"));
        assertThat(issues).anyMatch(i -> i.field().equals("urlSlug") && i.severity().equals("ERROR"));
    }

    @Test
    void aWellFormedSlugPassesCleanly() {
        List<ValidationIssue> issues = service.validate(
                contentWith("A".repeat(45), "meta", "A".repeat(140), "well-formed-slug-123"));
        assertThat(issues).noneMatch(i -> i.field().equals("urlSlug"));
    }

    @Test
    void flagsWhenNoKeywordAppearsInTheTitle() {
        GeneratedContent content = new GeneratedContent("A".repeat(45), "meta", "A".repeat(140), "short", "long",
                List.of("bullet"), List.of("totally-unrelated-term"), "valid-slug", List.of());
        List<ValidationIssue> issues = service.validate(content);
        assertThat(issues).anyMatch(i -> i.field().equals("seoTitle") && i.message().contains("No suggested keyword"));
    }
}
