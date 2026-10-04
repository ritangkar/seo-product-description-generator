# Design Decisions

---

**Decision:** Ship only the deterministic `DemoContentGenerator` — no
optional live-LLM generator, unlike the Personal Research Agent project
(Batch 1), which offers both a demo client and a gated live one.

**Why:** The Personal Research Agent's live-LLM option exists to
demonstrate a specific architectural pattern — citation validation that
holds regardless of which backend produced the text. This project's
`FactualityCheckService` already demonstrates the equivalent idea (it
re-derives ground truth from the input every time, never trusting the
generator), and it does so by taking the generated content and the
original attributes as two separate arguments — so the check would work
unmodified against a live-LLM generator's output too, if one were added.
Adding a second generator here would repeat a pattern already shown
elsewhere in this collection without teaching anything new, at the cost of
another dependency and another thing that can't be verified without a
live API key.

**Trade-off:** This project cannot produce more varied, naturalistic
phrasing than its fixed templates allow — a real deployment wanting that
would need to add a live generator, following the seam
`FactualityCheckService`'s two-argument signature already leaves open.

---

**Decision:** The factuality check specifically targets number+unit
patterns (e.g. "250g", "500ml", "10%"), not general claims.

**Why:** Checking that a specific, checkable fact (a weight, a capacity, a
percentage) traces back to the input is tractable with a regex and a
substring search — reliably catching the failure mode that matters most
(an invented spec). Checking that a vaguer claim like "great for everyday
use" is "true" isn't a well-defined problem a deterministic rule can
solve; the master brief is explicit that this stage must be deterministic
Java, so the check is scoped to what deterministic Java can actually verify.

**Trade-off:** A fabricated claim without a number+unit pattern (e.g. an
invented material name) wouldn't be caught by this check. Documented
directly, not implied to be broader coverage than it is.

---

**Decision:** `QualityScoreService`'s scoring formula and
`SeoValidationService`'s length thresholds are separate from — and not
tuned to match — the standalone Product SEO Score Calculator project
(a different project in this collection, evaluating arbitrary *existing*
content against its own, more detailed rubric).

**Why:** The two projects answer different questions ("is what I just
generated safe to publish" vs. "how good is this content someone already
has"), and conflating their scoring systems would blur that distinction
the master brief explicitly asks this collection to preserve. See that
project's own README once it exists for its scoring rubric.

**Trade-off:** A product run through both projects could get two
differently-scaled scores — expected and correct, not a bug, given they
measure different things.
