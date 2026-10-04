# SEO-Optimized Product Description Generator

A focused AI commerce utility: **Product Data → Attribute Extraction →
Keyword Analysis → Content Generation → SEO Validation → Factuality Check
→ Quality Score.** Runs entirely without an API key in demo mode, and the
validation stages are always deterministic Java — regardless of what
generates the content.

Part of the [Commerce Engineering Lab](https://ritangkar.github.io/#commerce-lab) —
17 small, production-minded engineering capabilities by Ritangkar Dey.

---

## 1. Project Overview

Send structured product data (name, category, brand, features,
specifications, target audience, keywords, tone); get back an SEO title,
meta title/description, short/long descriptions, feature bullets,
suggested keywords, a URL slug, FAQ content, a list of SEO issues, a list
of factuality issues, and a 0–100 quality score.

## 2. Problem

Generated product copy is only useful if it's actually safe to publish:
the right length for search snippets, a clean URL slug, and — critically —
never asserting a spec the product doesn't actually have. A generator
alone can't guarantee any of that; validation has to be separate and
trustworthy regardless of what generated the text.

## 3. Solution

A deterministic template generator (four tones) produces the content;
two independent, always-deterministic Java stages — SEO validation and a
factuality check that re-derives ground truth from the original input
every time — grade it; a simple weighted formula turns both into one score.

## 4. Key Features

- Four tones (professional, casual, playful, luxury), each with genuinely
  different phrasing for the same product
- Deterministic SEO checks: title/meta-description length, slug format,
  keyword presence
- **Factuality check**: flags any number+unit claim ("250g", "500ml",
  "10%") in the generated text that doesn't trace back to the actual
  supplied features or specifications — proven with a deliberately
  fabricated test case, not just shown passing on well-behaved input
- FAQ content generated directly from the specifications map — never invented
- A composite quality score with a visible, itemized breakdown

## 5. Architecture

```
ProductInputData → AttributeExtraction → KeywordAnalysis → DemoContentGenerator
                                                                  │
                                              ┌───────────────────┴───────────────────┐
                                              ▼                                       ▼
                                      SeoValidationService                FactualityCheckService
                                              └───────────────────┬───────────────────┘
                                                                  ▼
                                                         QualityScoreService → SeoContentResult
```

See [`docs/architecture.md`](docs/architecture.md) for the full diagram.

## 6. Technical Approach

- `DemoContentGenerator` only ever echoes input data into templates — it
  cannot invent a spec, by construction
- `FactualityCheckService` re-derives ground truth from the *original*
  input on every check, rather than trusting the generator — so the same
  safety net would hold even if a different generator were substituted later

## 7. Design Decisions

See [`docs/design-decisions.md`](docs/design-decisions.md) — including
why this project ships only a demo generator (unlike the Personal
Research Agent project) and how its scoring is deliberately kept separate
from the standalone SEO Score Calculator project's rubric.

## 8. Sample Input

```json
{
  "name": "Drift 271", "category": "Footwear", "brand": "ZenithWear",
  "features": ["Breathable mesh upper", "Cushioned midsole"],
  "specifications": {"Weight": "250g", "Material": "Mesh/EVA"},
  "targetAudience": "long-distance runners",
  "keywords": ["running shoes"], "tone": "PROFESSIONAL"
}
```

## 9. Sample Output

Real, verified output for the input above:

```json
{
  "content": {
    "seoTitle": "ZenithWear Drift 271 \u2013 Breathable mesh upper",
    "metaTitle": "ZenithWear Drift 271",
    "metaDescription": "Shop the ZenithWear Drift 271, featuring Breathable mesh upper. Designed for long-distance runners. Cushioned midsole. Order today.",
    "urlSlug": "zenithwear-drift-271",
    "suggestedKeywords": ["running shoes", "footwear", "zenithwear", "zenithwear footwear", "breathable", "mesh", "cushioned", "midsole"]
  },
  "seoValidationIssues": [],
  "factualityIssues": [],
  "qualityScore": 100
}
```

*A fully compliant input scores a clean 100 — see the test suite for a
deliberately non-compliant example and the resulting deductions.*

## 10. How to Run

```bash
git clone <your-repo-url>
cd seo-product-description-generator
mvn spring-boot:run
```

API available at `http://localhost:8092`. No API key or environment
variable is required.

## 11. How to Test

```bash
mvn test
```

Covers the full pipeline against the verified reference scenario above,
tone-differentiation, SEO validation edge cases (too-short/too-long
titles, malformed slugs), and — most importantly — a deliberately
fabricated factuality-violation case proving the check actually catches
an unfounded claim, not just that it passes on good input.

## 12. API Documentation

| Method | Path | Description |
|---|---|---|
| POST | `/api/seo-content/generate` | `ProductInputData` → `SeoContentResult` |

## 13. Portfolio Demo

The Commerce Engineering Lab demo (`lab/demos/seo-product-description-generator.js`)
ports the full pipeline to client-side JavaScript. See [`docs/demo.md`](docs/demo.md).

## 14. Limitations

- Four fixed tones with fixed templates — not free-form generation
- The factuality check only catches number+unit claims, not all possible
  fabrications (e.g. an invented material name) — stated plainly in design decisions
- No live LLM option in this repository (see design decisions for why,
  and where the seam for adding one already exists)

## 15. Future Enhancements

- An optional live-LLM generator behind the same `FactualityCheckService` seam
- Multi-language template sets
- A/B comparison mode: generate the same product in two tones side by side

## 16. License

MIT — see [`LICENSE`](LICENSE).
