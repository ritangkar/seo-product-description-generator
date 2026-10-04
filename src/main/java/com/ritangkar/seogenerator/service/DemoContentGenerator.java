package com.ritangkar.seogenerator.service;

import com.ritangkar.seogenerator.model.ExtractedAttributes;
import com.ritangkar.seogenerator.model.FaqItem;
import com.ritangkar.seogenerator.model.GeneratedContent;
import com.ritangkar.seogenerator.model.Tone;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The default content generator: fully deterministic, template-based,
 * tone-aware. Requires no network access and no API key — this is what
 * makes the whole pipeline runnable with zero setup. Every sentence is
 * built directly from {@link ExtractedAttributes}, so the Factuality
 * Check stage should never need to strip anything this generator produces
 * (see FactualityCheckService and its test suite).
 */
@Component
public class DemoContentGenerator {

    public GeneratedContent generate(ExtractedAttributes a, List<String> keywords) {
        String secondFeature = a.features().size() > 1 ? a.features().get(1) : null;

        String seoTitle = titleFor(a);
        String metaTitle = a.brand() + " " + a.name();
        String metaDescription = metaDescriptionFor(a, secondFeature);
        String shortDescription = shortDescriptionFor(a);
        String longDescription = longDescriptionFor(a);
        List<String> bullets = a.features().stream().map(f -> f + " — built into every " + a.name() + ".").toList();
        String slug = slugify(a.brand() + " " + a.name());
        List<FaqItem> faqs = faqsFor(a);

        return new GeneratedContent(seoTitle, metaTitle, metaDescription, shortDescription, longDescription,
                bullets, keywords, slug, faqs);
    }

    private String titleFor(ExtractedAttributes a) {
        return switch (a.tone()) {
            case PROFESSIONAL -> a.brand() + " " + a.name() + " \u2013 " + a.primaryFeature();
            case CASUAL -> a.brand() + " " + a.name() + ": " + a.primaryFeature();
            case PLAYFUL -> a.brand() + " " + a.name() + " \u2013 " + a.primaryFeature() + "!";
            case LUXURY -> a.brand() + " " + a.name() + " | " + a.primaryFeature();
        };
    }

    private String metaDescriptionFor(ExtractedAttributes a, String secondFeature) {
        String second = secondFeature != null ? " " + secondFeature + "." : "";
        return switch (a.tone()) {
            case PROFESSIONAL -> "Shop the " + a.brand() + " " + a.name() + ", featuring " + a.primaryFeature()
                    + ". Designed for " + a.targetAudience() + "." + second + " Order today.";
            case CASUAL -> "Meet the " + a.brand() + " " + a.name() + " \u2014 " + a.primaryFeature()
                    + ", made for " + a.targetAudience() + "." + second;
            case PLAYFUL -> "Say hello to the " + a.brand() + " " + a.name() + "! " + a.primaryFeature()
                    + " and ready for " + a.targetAudience() + "." + second;
            case LUXURY -> "Introducing the " + a.brand() + " " + a.name() + " \u2014 where " + a.primaryFeature()
                    + " meets uncompromising craftsmanship, curated for " + a.targetAudience() + "." + second;
        };
    }

    private String shortDescriptionFor(ExtractedAttributes a) {
        return switch (a.tone()) {
            case PROFESSIONAL -> a.brand() + " " + a.name() + " delivers " + a.primaryFeature()
                    + " for " + a.targetAudience() + ", engineered for reliable everyday performance.";
            case CASUAL -> "Meet the " + a.brand() + " " + a.name() + " \u2014 " + a.primaryFeature()
                    + " that just gets it done for " + a.targetAudience() + ".";
            case PLAYFUL -> "Say hello to the " + a.brand() + " " + a.name() + "! " + a.primaryFeature()
                    + " and ready to have some fun, perfect for " + a.targetAudience() + ".";
            case LUXURY -> "Introducing the " + a.brand() + " " + a.name() + " \u2014 where " + a.primaryFeature()
                    + " meets uncompromising craftsmanship, curated for " + a.targetAudience() + ".";
        };
    }

    private String longDescriptionFor(ExtractedAttributes a) {
        StringBuilder sb = new StringBuilder();
        sb.append(shortDescriptionFor(a)).append("\n\n");

        if (!a.features().isEmpty()) {
            sb.append("Key features include ");
            sb.append(String.join(", ", a.features()));
            sb.append(".\n\n");
        }

        if (!a.specifications().isEmpty()) {
            sb.append("Specifications: ");
            List<String> specParts = new ArrayList<>();
            a.specifications().forEach((k, v) -> specParts.add(k + ": " + v));
            sb.append(String.join("; ", specParts)).append(".\n\n");
        }

        sb.append("The ").append(a.brand()).append(" ").append(a.name())
                .append(" is built for ").append(a.targetAudience())
                .append(" who expect ").append(a.category().toLowerCase()).append(" that performs.");

        return sb.toString();
    }

    private List<FaqItem> faqsFor(ExtractedAttributes a) {
        List<FaqItem> faqs = new ArrayList<>();
        faqs.add(new FaqItem("Who is the " + a.name() + " designed for?", "The " + a.brand() + " " + a.name()
                + " is designed for " + a.targetAudience() + "."));

        if (!a.features().isEmpty()) {
            faqs.add(new FaqItem("What are the key features of the " + a.name() + "?",
                    String.join(", ", a.features()) + "."));
        }

        a.specifications().forEach((key, value) ->
                faqs.add(new FaqItem("What is the " + key.toLowerCase() + " of the " + a.name() + "?", value + ".")));

        return faqs;
    }

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    private String slugify(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        String stripped = NON_ALPHANUMERIC.matcher(lower).replaceAll("");
        String hyphenated = WHITESPACE.matcher(stripped.trim()).replaceAll("-");
        return MULTI_HYPHEN.matcher(hyphenated).replaceAll("-");
    }
}
