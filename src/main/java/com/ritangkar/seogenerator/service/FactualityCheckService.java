package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.ExtractedAttributes;
import com.ritangkar.seogenerator.model.FaqItem;
import com.ritangkar.seogenerator.model.GeneratedContent;
import com.ritangkar.seogenerator.model.ValidationIssue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans every piece of generated text for a number-plus-unit pattern
 * (e.g. "10kg", "2.5L", "100%") and checks that exact token appears
 * somewhere in the input data (a specification value or a feature
 * string). If it doesn't, the content is asserting a specific, checkable
 * fact this pipeline was never given — flagged regardless of which
 * content generator produced it, demo or (if ever enabled) a live LLM.
 *
 * The demo generator only ever echoes input data verbatim, so in demo
 * mode this check should always pass — see DemoContentGeneratorTest for
 * confirmation, and FactualityCheckServiceTest for a deliberately
 * fabricated example that proves the check actually catches something.
 */
@Component
public class FactualityCheckService {

    private static final Pattern NUMBER_WITH_UNIT = Pattern.compile(
            "\\b\\d+(\\.\\d+)?\\s?(kg|g|mm|cm|m|l|ml|w|kw|mah|gb|tb|hrs?|hours?|%|inch(es)?|lbs?)\\b",
            Pattern.CASE_INSENSITIVE);

    public List<ValidationIssue> check(GeneratedContent content, ExtractedAttributes source) {
        String groundTruth = buildGroundTruth(source);
        List<ValidationIssue> issues = new ArrayList<>();

        checkText(issues, "longDescription", content.longDescription(), groundTruth);
        checkText(issues, "metaDescription", content.metaDescription(), groundTruth);
        content.featureBullets().forEach(b -> checkText(issues, "featureBullets", b, groundTruth));
        for (FaqItem faq : content.faqItems()) {
            checkText(issues, "faqItems", faq.answer(), groundTruth);
        }

        return issues;
    }

    private void checkText(List<ValidationIssue> issues, String field, String text, String groundTruth) {
        Matcher matcher = NUMBER_WITH_UNIT.matcher(text);
        while (matcher.find()) {
            String claim = matcher.group().trim();
            if (!groundTruth.contains(claim.toLowerCase())) {
                issues.add(new ValidationIssue(field, "ERROR",
                        "Generated text states \"" + claim + "\", which does not appear anywhere in the "
                                + "supplied features or specifications \u2014 unfounded claim."));
            }
        }
    }

    private String buildGroundTruth(ExtractedAttributes source) {
        StringBuilder sb = new StringBuilder();
        source.features().forEach(f -> sb.append(f.toLowerCase()).append(" "));
        source.specifications().values().forEach(v -> sb.append(v.toLowerCase()).append(" "));
        return sb.toString();
    }
}
