package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FactualityCheckServiceTest {

    private final FactualityCheckService service = new FactualityCheckService();

    private ExtractedAttributes attributesWith(List<String> features, Map<String, String> specs) {
        return new ExtractedAttributes("Test Product", "Accessories", "TestBrand", features,
                features.isEmpty() ? "quality" : features.get(0), specs, "everyone", List.of(), Tone.PROFESSIONAL);
    }

    @Test
    void passesWhenEveryClaimTracesBackToSuppliedData() {
        ExtractedAttributes attrs = attributesWith(List.of("Comfortable fit"), Map.of("Weight", "250g"));
        GeneratedContent content = new GeneratedContent(
                "title", "meta title", "meta description",
                "short description", "The product weighs 250g and offers Comfortable fit.",
                List.of("Comfortable fit — a great feature."), List.of(), "slug",
                List.of(new FaqItem("What is the weight?", "250g.")));

        List<ValidationIssue> issues = service.check(content, attrs);
        assertThat(issues).isEmpty();
    }

    @Test
    void flagsAFabricatedSpecificationNotPresentAnywhereInTheInput() {
        ExtractedAttributes attrs = attributesWith(List.of("Comfortable fit"), Map.of());
        GeneratedContent content = new GeneratedContent(
                "title", "meta title", "meta description", "short description",
                "This product holds an impressive 500ml capacity.", // fabricated — not in features/specs
                List.of(), List.of(), "slug", List.of());

        List<ValidationIssue> issues = service.check(content, attrs);

        assertThat(issues).isNotEmpty();
        assertThat(issues).anyMatch(i -> i.message().contains("500ml"));
        assertThat(issues).allMatch(i -> i.severity().equals("ERROR"));
    }

    @Test
    void aFabricatedClaimInAFaqAnswerIsAlsoCaught() {
        ExtractedAttributes attrs = attributesWith(List.of("Durable build"), Map.of());
        GeneratedContent content = new GeneratedContent(
                "title", "meta title", "meta description", "short description", "long description",
                List.of(), List.of(), "slug",
                List.of(new FaqItem("How much does it weigh?", "It weighs only 90g.")));

        List<ValidationIssue> issues = service.check(content, attrs);
        assertThat(issues).anyMatch(i -> i.field().equals("faqItems") && i.message().contains("90g"));
    }

    @Test
    void numbersWithoutARecognizedUnitAreNotFlagged() {
        // "271" (a model number) has no unit suffix, so it's not the kind of
        // checkable factual claim this check targets — see class javadoc.
        ExtractedAttributes attrs = attributesWith(List.of("Model 271 design"), Map.of());
        GeneratedContent content = new GeneratedContent(
                "title", "meta title", "meta description", "short description",
                "The Model 271 design is a bestseller.", List.of(), List.of(), "slug", List.of());

        List<ValidationIssue> issues = service.check(content, attrs);
        assertThat(issues).isEmpty();
    }
}
