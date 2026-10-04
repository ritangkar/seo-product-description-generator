package com.ritangkar.seogenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SEO-Optimized Product Description Generator.
 *
 * Product Data -> Attribute Extraction -> Keyword Analysis -> Content
 * Generation -> SEO Validation -> Factuality Check -> Quality Score.
 *
 * Runs entirely in demo mode by default: {@code DemoContentGenerator} is
 * deterministic, template-based, and requires no network access or API
 * key. The SEO validation and factuality-check stages are always
 * deterministic Java rules, regardless of which content generator
 * produced the text — see docs/design-decisions.md.
 *
 * Distinct from the separate Product SEO Score Calculator project: this
 * project *creates* content; that one *scores* content someone already has.
 */
@SpringBootApplication
public class SeoGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeoGeneratorApplication.class, args);
    }
}
