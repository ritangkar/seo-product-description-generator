package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.*;
import org.springframework.stereotype.Service;

/**
 * Product Data -&gt; Attribute Extraction -&gt; Keyword Analysis -&gt; Content
 * Generation -&gt; SEO Validation -&gt; Factuality Check -&gt; Quality Score.
 *
 * Every stage after Content Generation is deterministic Java, regardless
 * of which generator produced the content — see docs/design-decisions.md.
 */
@Service
public class SeoContentPipelineService {

    private final AttributeExtractionService attributeExtractionService;
    private final KeywordAnalysisService keywordAnalysisService;
    private final DemoContentGenerator demoContentGenerator;
    private final SeoValidationService seoValidationService;
    private final FactualityCheckService factualityCheckService;
    private final QualityScoreService qualityScoreService;

    public SeoContentPipelineService(AttributeExtractionService attributeExtractionService,
                                      KeywordAnalysisService keywordAnalysisService,
                                      DemoContentGenerator demoContentGenerator,
                                      SeoValidationService seoValidationService,
                                      FactualityCheckService factualityCheckService,
                                      QualityScoreService qualityScoreService) {
        this.attributeExtractionService = attributeExtractionService;
        this.keywordAnalysisService = keywordAnalysisService;
        this.demoContentGenerator = demoContentGenerator;
        this.seoValidationService = seoValidationService;
        this.factualityCheckService = factualityCheckService;
        this.qualityScoreService = qualityScoreService;
    }

    public SeoContentResult run(ProductInputData input) {
        ExtractedAttributes attributes = attributeExtractionService.extract(input);
        var keywords = keywordAnalysisService.analyze(attributes);
        GeneratedContent content = demoContentGenerator.generate(attributes, keywords);

        var seoIssues = seoValidationService.validate(content);
        var factualityIssues = factualityCheckService.check(content, attributes);
        var scoreResult = qualityScoreService.score(seoIssues, factualityIssues);

        return new SeoContentResult(content, seoIssues, factualityIssues,
                scoreResult.score(), scoreResult.breakdown());
    }
}
