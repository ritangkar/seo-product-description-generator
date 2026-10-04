package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.ProductInputData;
import com.ritangkar.seogenerator.model.SeoContentResult;
import com.ritangkar.seogenerator.model.Tone;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every length, slug and score figure below was independently computed
 * with a Python reference implementation of the same templates and
 * rules before this test was written — see docs/design-decisions.md.
 */
class SeoContentPipelineServiceTest {

    private final AttributeExtractionService attributeExtractionService = new AttributeExtractionService();
    private final KeywordAnalysisService keywordAnalysisService = new KeywordAnalysisService();
    private final DemoContentGenerator demoContentGenerator = new DemoContentGenerator();
    private final SeoValidationService seoValidationService = new SeoValidationService();
    private final FactualityCheckService factualityCheckService = new FactualityCheckService();
    private final QualityScoreService qualityScoreService = new QualityScoreService();
    private final SeoContentPipelineService pipeline = new SeoContentPipelineService(
            attributeExtractionService, keywordAnalysisService, demoContentGenerator,
            seoValidationService, factualityCheckService, qualityScoreService);

    private ProductInputData referenceInput() {
        return new ProductInputData(
                "Drift 271", "Footwear", "ZenithWear",
                List.of("Breathable mesh upper", "Cushioned midsole"),
                Map.of("Weight", "250g", "Material", "Mesh/EVA"),
                "long-distance runners",
                List.of("running shoes"),
                Tone.PROFESSIONAL
        );
    }

    @Test
    void generatesTitlesWithinTheVerifiedLengthAndContent() {
        SeoContentResult result = pipeline.run(referenceInput());

        assertThat(result.content().seoTitle()).isEqualTo("ZenithWear Drift 271 \u2013 Breathable mesh upper");
        assertThat(result.content().seoTitle()).hasSize(44);
        assertThat(result.content().metaTitle()).isEqualTo("ZenithWear Drift 271");
    }

    @Test
    void generatesAMetaDescriptionWithinTheRecommendedLengthRange() {
        SeoContentResult result = pipeline.run(referenceInput());
        int length = result.content().metaDescription().length();

        assertThat(length).isEqualTo(131);
        assertThat(length).isBetween(120, 160);
    }

    @Test
    void generatesAWellFormedSlug() {
        SeoContentResult result = pipeline.run(referenceInput());
        assertThat(result.content().urlSlug()).isEqualTo("zenithwear-drift-271");
    }

    @Test
    void aFullyCompliantInputScoresAPerfect100WithNoIssues() {
        SeoContentResult result = pipeline.run(referenceInput());

        assertThat(result.seoValidationIssues()).isEmpty();
        assertThat(result.factualityIssues()).isEmpty();
        assertThat(result.qualityScore()).isEqualTo(100);
    }

    @Test
    void everyFaqAnswerIsGroundedInSuppliedData() {
        SeoContentResult result = pipeline.run(referenceInput());

        assertThat(result.content().faqItems())
                .anyMatch(f -> f.answer().contains("250g"))
                .anyMatch(f -> f.answer().contains("long-distance runners"));
    }

    @Test
    void keywordAnalysisIncludesBothProvidedAndDerivedTerms() {
        SeoContentResult result = pipeline.run(referenceInput());

        assertThat(result.content().suggestedKeywords())
                .contains("running shoes")  // provided
                .contains("footwear")       // derived from category
                .contains("breathable");    // derived from a feature
    }

    @Test
    void differentTonesProduceDifferentPhrasingForTheSameProduct() {
        ProductInputData professional = referenceInput();
        ProductInputData playful = new ProductInputData(
                professional.name(), professional.category(), professional.brand(), professional.features(),
                professional.specifications(), professional.targetAudience(), professional.keywords(), Tone.PLAYFUL);

        String proTitle = pipeline.run(professional).content().seoTitle();
        String playfulTitle = pipeline.run(playful).content().seoTitle();

        assertThat(proTitle).isNotEqualTo(playfulTitle);
        assertThat(playfulTitle).endsWith("!");
    }
}
