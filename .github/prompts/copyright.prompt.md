---
description: Update or add copyright headers to changed files
---

# Copyright Agent

Check git changed files and update/add copyright headers.

## Configuration
```
COPYRIGHT_HOLDER = "Progress Software Corporation and/or its subsidiaries or affiliates"
COPYRIGHT_START_YEAR = "auto"
```

## Copyright Format
```
Copyright (c) {START_YEAR}-{CURRENT_YEAR} {COPYRIGHT_HOLDER}. All Rights Reserved.
```

## Start Year Detection
1. **Existing copyright in file**: Preserve the start year, update end year only
2. **New file + auto mode**: Find start year from other files in repo
   - If one consistent year found: Use it
   - If multiple years or none found: **ASK USER**
3. **Explicit year set**: Use that for new files

## Files Needing Copyright
| Extension | Comment Style |
|-----------|---------------|
| `.java`, `.groovy`, `.kt`, `.scala`, `.js`, `.ts`, `.tsx`, `.c`, `.cpp`, `.h`, `.go`, `.rs`, `.swift`, `.cs` | `/* ... */` |
| `.py`, `.rb`, `.sh`, `.bash`, `.pl`, `.ps1`, `.r` | `# ...` |
| `.sql`, `.lua`, `.hs`, `.vhd` | `-- ...` |
| `.xqy`, `.xq`, `.xquery` | `(: ... :)` |
| `.xml`, `.xsl` | `<!-- ... -->` |
| `.erl` | `%% ...` |
| `.clj`, `.lisp` | `;;; ...` |

## Files to SKIP
`build.gradle`, `pom.xml`, `settings.gradle`, `*.properties`, `*.json`, `*.yml`, `*.yaml`, `*.md`, `*.txt`, `*.html`, `*.css`, `Dockerfile`, `Jenkinsfile`, `.gitignore`, `gradlew`, lock files, `node_modules/`, `build/`, `target/`

## Actions
1. Check git changed files (staged + unstaged)
2. For each source file:
   - Has copyright with correct holder → Update end year to current year
   - Missing copyright → Add header with correct comment style
   - Different copyright holder → Skip (third-party)
3. Report summary table of actions taken

Never guess start year. Ask user when uncertain.
