# Copilot Crawl Track — README

This directory (`.copilot-track/crawl/`) holds AI-assisted crawl-and-modernisation artefacts for the **MarkLogic Java Client API** repository. It is not part of the production library; contents are for developer guidance and AI context only.

---

## What Is the Crawl Track?

The crawl track is an incremental, evidence-driven workflow for making large-scale changes to this codebase using AI assistance (GitHub Copilot / agent mode). Changes are broken into small, reviewable PRs that form a **chain** — each PR builds on the previous one.

---

## Chain-PR Pattern

A chain-PR is a sequence of pull requests where each PR:

1. Targets the **previous PR's branch** (not `main`) as its base, creating a linear dependency chain.
2. Carries a **single, focused concern** (e.g., "migrate HTTP client from HttpClient to OkHttp", "update Jackson version", "replace deprecated API calls in DocMgr").
3. Is reviewed and merged in order — do **not** merge PR N+1 before PR N is merged and its branch updated.

```
main ← PR-1 (foundation) ← PR-2 (layer A) ← PR-3 (layer B) ← ...
```

When the base PR merges, rebase subsequent PRs down the chain to keep them conflict-free:

```bash
git fetch origin
git checkout feature/crawl-layer-B
git rebase origin/feature/crawl-layer-A
git push --force-with-lease
```

---

## Evidence in PRs

Every crawl PR must include evidence that the change is safe. Accepted evidence types:

| Evidence                                         | Where to add it                                                            |
| ------------------------------------------------ | -------------------------------------------------------------------------- |
| Passing CI green-check (unit + functional tests) | Shown automatically on the PR by Jenkins                                   |
| Before/after compile output                      | Paste in PR description under `## Build evidence`                          |
| Test-coverage delta                              | Add `## Test delta` section; attach Gradle test report if coverage dropped |
| Copilot prompt used                              | Add `## Prompt used` section (see below)                                   |
| Manual verification steps                        | Add `## Manual verification` with exact commands run                       |

PRs that lack evidence will be marked **needs-evidence** and not merged.

---

## Prompt Usage

AI prompts that drove a crawl change belong in the PR description under `## Prompt used`. This creates an audit trail and lets reviewers reproduce or adjust the change.

**Template:**

```markdown
## Prompt used

> Agent mode, model: claude-sonnet-4-6
>
> "Migrate all usages of `com.marklogic.client.impl.OkHttpServices` constructor that
> pass a plain `String` password to instead use `char[]` and call `Arrays.fill` after use.
> Do not modify test files. Only change files under marklogic-client-api/src/main/."

Files changed by prompt: <!-- list them -->
Files reviewed manually: <!-- list them -->
```

Storing prompts in PRs helps future crawl passes understand _why_ a change was made, not just _what_ changed.

---

## Adding New Crawl Artefacts

Place any generated files, diff summaries, or migration notes inside this directory as flat Markdown or JSON files. Suggested naming:

```
.copilot-track/crawl/
├── README.md             ← this file
├── 001-<topic>.md        ← plan / notes for crawl step 1
├── 002-<topic>.md        ← plan / notes for crawl step 2
└── ...
```

Keep each step file small (< 200 lines). Reference `ai-track-docs/` for system-level context.

---

## Related Docs

- [ai-track-docs/SYSTEM-OVERVIEW.md](../../ai-track-docs/SYSTEM-OVERVIEW.md) — what this project does and how it is structured
- [ai-track-docs/build-test.md](../../ai-track-docs/build-test.md) — how to build and run tests locally
- [ai-track-docs/architecture.mmd](../../ai-track-docs/architecture.mmd) — Mermaid architecture diagram
