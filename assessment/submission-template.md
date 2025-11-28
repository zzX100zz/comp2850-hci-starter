# COMP2850 HCI Assessment: Evaluation & Redesign Portfolio

> **📥 Download this template**: [COMP2850-submission-template.md](/downloads/COMP2850-submission-template.md)
> Right-click the link above and select "Save link as..." to download the template file.

**Student**: Weiming Chen 201778231
**Submission date**: [DD/MM/YYYY]
**Academic Year**: 2025-26

---

## Privacy & Ethics Statement

- [x] I confirm all participant data is anonymous (session IDs use P1_xxxx format, not real names)
- [x] I confirm all screenshots are cropped/blurred to remove PII (no names, emails, student IDs visible)
- [x] I confirm all participants gave informed consent
- [x] I confirm this work is my own (AI tools used for code assistance are cited below)

**AI tools used** (if any): [e.g., "Copilot for route handler boilerplate (lines 45-67 in diffs)"]

---

## 1. Protocol & Tasks

### Link to Needs-Finding (LO2)

**Week 6 Job Story #1**:
> When I remember a deadline while listening to a lecture, I want to add a task quickly, so I can review it later, because I will forget it if I don't write it down immediately.

**How Task 1 tests this**:
> Task 1 (Add Task) directly tests this story by measuring if a user can successfully capture a new task ("Buy milk") within a short timeframe (<10s).

**Week 6 Job Story #2**:
> When I have finished an assignment, I want to remove it from my list, so I can focus on what is left, because a cluttered list makes me feel anxious.

**How Task 3 tests this**:
> Task 3 (Delete Task) tests this story by verifying if the user can easily locate and remove a completed item to declutter their view.

**Week 6 Job Story #3**:
> When I have a long list of accumulated tasks and deadlines, I want to quickly find a specific task (like "Submit Portfolio"), so I can check its status or mark it as complete without scrolling endlessly, because searching through a cluttered list causes me anxiety and wastes my time.

**How Task 2 tests this**:
> Task 2 (Find Task) tests this by measuring if the user can locate a specific item within the target time (< 5 seconds) using visual scanning or filter features, verifying the interface scales well with content.

**Week 6 Job Story #4**:
> When I am using an old library computer or a secure terminal where JavaScript is disabled for security, I want to still be able to add and view my tasks, so I can capture important to-dos immediately regardless of the device I am using, because I cannot rely on always having my personal smartphone or laptop with me.

**How Task 4 tests this**:
> Task 4 (No-JS Fallback) tests this by verifying that core functions (Add/Delete) remain operational via server-side rendering (POST-Redirect-GET pattern) when client-side scripting is unavailable. 

---

### Evaluation Tasks (4-5 tasks)

#### Task 1 (T1): Add Task

- **Scenario**: You are in a lecture and suddenly remember you need to buy milk on your way home.
- **Action**: Add a new task with the title "Buy milk".
- **Success**: The task "Buy milk" appears in your task list at the bottom.
- **Target time**: < 8 seconds
- **Linked to**: Week 6 Job Story #1

#### Task 2 (T2): Find Task

- **Scenario**: Your list is getting long and you need to check if you already wrote down the deadline for your coursework.
- **Action**: Find the task labeled "Submit HCI Portfolio".
- **Success**: User verbalizes they have found the task or points to it.
- **Target time**: < 5 seconds
- **Linked to**: Week 6 Job Story #3

#### Task 3 (T3):  Delete Task

- **Scenario**: You have just bought the milk and returned home.
- **Action**: Delete the "Buy milk" task from your list.
- **Success**: The "Buy milk" task disappears from the list without errors.
- **Target time**: < 5 seconds
- **Linked to**: Week 6 Job Story #2

#### Task 4 (T4):  No-JS Fallback

- **Scenario**: You are using an old library computer where JavaScript is disabled for security.
- **Action**: Try to add another task "Return books".
- **Success**: The page reloads, and the task "Return books" appears in the list.
- **Target time**: < 10 seconds
- **Linked to**: Week 6 Job Story #4

---

### Consent Script (They Read Verbatim)

**Introduction**:
"Thank you for participating in my HCI evaluation. This will take about 15 minutes. I'm testing my task management interface, not you. There are no right or wrong answers."

**Rights**:
- [x] "Your participation is voluntary. You can stop at any time without giving a reason."
- [x] "Your data will be anonymous. I'll use a code (like P1) instead of your name."
- [x] "I may take screenshots and notes. I'll remove any identifying information."
- [x] "Do you consent to participate?" [Wait for verbal yes]

**Recorded consent timestamp**: [e.g., "P1 consented 22/11/2025 14:05"]

---

## 2. Findings Table

**Instructions**: Fill in this table with 3-5 findings from your pilots. Link each finding to data sources.

| Finding | Data Source | Observation (Quote/Timestamp) | WCAG | Impact (1-5) | Inclusion (1-5) | Effort (1-5) | Priority |
|---------|-------------|------------------------------|------|--------------|-----------------|--------------|----------|
| Double submission of tasks | metrics.csv rows 5-6 (P3 added twice in 1s) | P3 added "Buy milk" twice. Observation: "Clicked add, didn't see it immediately, clicked again." | Nielsen #1 Visibility of System Status | 3 | 1 | 1 | 5 |
| Success status silent for SR | Code Inspection (TaskRoutes.kt) + P3 confusion | Success message div lacks role="status" or aria-live. | WCAG 4.1.3 Status Messages (AA) | 3 | 5 | 1 | 9 |
| Broken Image | Week 7 Debugging | Image failed to load due to space in tag (< img). | WCAG 4.1.1 Parsing(A)| 2 | 2 | 1 | 3 |


**Priority formula**: (Impact + Inclusion) - Effort

**Top 3 priorities for redesign**:
1.  Fix Success Status (Add ARIA role) - Priority 9
2. Prevent Double Submission (Add visual feedback/disable button) - Priority 5
3. Fix Image Syntax (Priority 3) - Simple code cleanup to restore visuals.

---

## 3. Pilot Metrics (metrics.csv)

**Instructions**: Paste your raw CSV data here OR attach metrics.csv file

```csv
ts_iso,session_id,request_id,task_code,step,outcome,ms,http_status,js_mode
2025-11-28T02:41:38.365395500Z,P1_Mouse,ba827e5d,T1_Add,persist,success,10,201,on
2025-11-28T02:41:46.541890500Z,P1_Mouse,9a548ed3,T3_Delete,persist,success,1,200,on
2025-11-28T02:44:27.037988600Z,P2_Keyboard,fe61629d,T1_Add,persist,success,12,201,on
2025-11-28T02:44:41.492445800Z,P2_Keyboard,d9f711b8,T3_Delete,persist,success,2,200,on
2025-11-28T02:46:05.301071900Z,P3_Mouse,02b977d1,T1_Add,persist,success,2,201,on
2025-11-28T02:46:06.754112700Z,P3_Mouse,948395cb,T1_Add,persist,success,1,201,on
2025-11-28T02:46:16.329602500Z,P3_Mouse,e94b66f2,T3_Delete,persist,success,0,200,on
2025-11-28T02:46:18.844935Z,P3_Mouse,b14fefc5,T3_Delete,persist,success,1,200,on
```

**Participant summary**:
- **P1**: [Variant - e.g., "Standard mouse + HTMX"]
- **P2**: [Variant - e.g., "Keyboard-only, HTMX-on"]
- **P3** (if applicable): [Variant]
- **P4** (if applicable): [Variant]

**Total participants**: [n=2, 3, or 4]

---

## 4. Implementation Diffs

**Instructions**: Show before/after code for 1-3 fixes. Link each to findings table.

### Fix 1: [Fix Name]

**Addresses finding**: [Finding #X from table above]

**Before** ([file path:line number]):
```kotlin
// ❌ Problem code
[Paste your original code here]
```

**After** ([file path:line number]):
```kotlin
// ✅ Fixed code
[Paste your improved code here]
```

**What changed**: [1 sentence - what you added/removed/modified]

**Why**: [1 sentence - which WCAG criterion or usability issue this fixes]

**Impact**: [1-2 sentences - how this improves UX, who benefits]

---

### Fix 2: [Fix Name]

**Addresses finding**: [Finding #X]

**Before**:
```kotlin
[Original code]
```

**After**:
```kotlin
[Fixed code]
```

**What changed**:

**Why**:

**Impact**:

---

[Add Fix 3 if applicable]

---

## 5. Verification Results

### Part A: Regression Checklist (20 checks)

**Instructions**: Test all 20 criteria. Mark pass/fail/n/a + add notes.

| Check | Criterion | Level | Result | Notes |
|-------|-----------|-------|--------|-------|
| **Keyboard (5)** | | | | |
| K1 | 2.1.1 All actions keyboard accessible | A | [pass/fail] | [e.g., "Tested Tab/Enter on all buttons"] |
| K2 | 2.4.7 Focus visible | AA | [pass/fail] | [e.g., "2px blue outline on all interactive elements"] |
| K3 | No keyboard traps | A | [pass/fail] | [e.g., "Can Tab through filter, edit, delete without traps"] |
| K4 | Logical tab order | A | [pass/fail] | [e.g., "Top to bottom, left to right"] |
| K5 | Skip links present | AA | [pass/fail/n/a] | [e.g., "Skip to main content works"] |
| **Forms (3)** | | | | |
| F1 | 3.3.2 Labels present | A | [pass/fail] | [e.g., "All inputs have <label> or aria-label"] |
| F2 | 3.3.1 Errors identified | A | [pass/fail] | [e.g., "Errors have role=alert (FIXED in Fix #1)"] |
| F3 | 4.1.2 Name/role/value | A | [pass/fail] | [e.g., "All form controls have accessible names"] |
| **Dynamic (3)** | | | | |
| D1 | 4.1.3 Status messages | AA | [pass/fail] | [e.g., "Status div has role=status"] |
| D2 | Live regions work | AA | [pass/fail] | [e.g., "Tested with NVDA, announces 'Task added'"] |
| D3 | Focus management | A | [pass/fail] | [e.g., "Focus moves to error summary after submit"] |
| **No-JS (3)** | | | | |
| N1 | Full feature parity | — | [pass/fail] | [e.g., "All CRUD ops work without JS"] |
| N2 | POST-Redirect-GET | — | [pass/fail] | [e.g., "No double-submit on refresh"] |
| N3 | Errors visible | A | [pass/fail] | [e.g., "Error summary shown in no-JS mode"] |
| **Visual (3)** | | | | |
| V1 | 1.4.3 Contrast minimum | AA | [pass/fail] | [e.g., "All text 7.1:1 (AAA) via CCA"] |
| V2 | 1.4.4 Resize text | AA | [pass/fail] | [e.g., "200% zoom, no content loss"] |
| V3 | 1.4.11 Non-text contrast | AA | [pass/fail] | [e.g., "Focus indicator 4.5:1"] |
| **Semantic (3)** | | | | |
| S1 | 1.3.1 Headings hierarchy | A | [pass/fail] | [e.g., "h1 → h2 → h3, no skips"] |
| S2 | 2.4.1 Bypass blocks | A | [pass/fail] | [e.g., "<main> landmark, <nav> for filter"] |
| S3 | 1.1.1 Alt text | A | [pass/fail] | [e.g., "No images in interface OR all have alt"] |

**Summary**: [X/20 pass], [Y/20 fail]
**Critical failures** (if any): [List any Level A fails]

---

### Part B: Before/After Comparison

**Instructions**: Compare Week 9 baseline (pre-fix) to Week 10 (post-fix). Show improvement.

| Metric | Before (Week 9, n=X) | After (Week 10, n=Y) | Change | Target Met? |
|--------|----------------------|----------------------|--------|-------------|
| SR error detection | [e.g., 0/2 (0%)] | [e.g., 2/2 (100%)] | [e.g., +100%] | [✅/❌] |
| Validation error rate | [e.g., 33%] | [e.g., 0%] | [e.g., -33%] | [✅/❌] |
| Median time [Task X] | [e.g., 1400ms] | [e.g., 1150ms] | [e.g., -250ms] | [✅/❌] |
| WCAG [criterion] pass | [fail] | [pass] | [— ] | [✅/❌] |

**Re-pilot details**:
- **P5** (post-fix): [Variant - e.g., "Screen reader user, NVDA + keyboard"] - [Date piloted]
- **P6** (if applicable): [Variant] - [Date]

**Limitations / Honest reporting**:
[If metrics didn't improve or only modestly: explain why. Small sample size? Wrong fix? Needs more iteration? Be honest - valued over perfect results.]

---

## 6. Evidence Folder Contents

**Instructions**: List all files in your evidence/ folder. Provide context.

### Screenshots

| Filename | What it shows | Context/Link to finding |
|----------|---------------|-------------------------|
| wk9-validation-error.png | Yellow error message "Please enter a string" | Proof that validation logic works (P3 testing) |
| wk9-double-add-log.png | Screenshot of metrics.csv rows 5-6 | Finding #1: Double submission timestamps |
| wk7-broken-image-syntax.png | Image tag has syntax error (< img) | Finding #3: Syntax Error causing broken image |

**PII check**:
- [x] All screenshots cropped to show only relevant UI
- [x] Browser bookmarks/tabs not visible
- [x] Participant names/emails blurred or not visible

---

### Pilot Notes

**Instructions**: Attach pilot notes as separate files (P1-notes.md, P2-notes.md, etc.). Summarize key observations here.

**P1** (Mouse):
- **Key observation 1**: Smooth completion. Add time 10ms. No issues found.

**P2** (Keyboard):
- Navigated using Tab/Enter.
- **Key observation 1**: Focus rings were visible.
- **Key observation 2**: Completed Add/Delete cycle efficiently (12ms add time).

[Repeat for P3, P4 if applicable]

**P3** (Mouse):
- **Key observation 1**: At 02:46:05, P3 clicked "Add". Seemed unsure if it worked, clicked again 1.4s later.
- Resulted in duplicate tasks.
- Deleted both duplicates afterwards.
- Triggered validation error intentionally to test robustness (see screenshot).
---

## Evidence Chain Example (Full Trace)

**Instructions**: Pick ONE finding and show complete evidence trail from data → fix → verification.

**Finding selected**: [e.g., "Finding #1 - SR errors not announced"]

**Evidence trail**:
1. **Data**: metrics.csv lines 47-49 show P2 (SR user) triggered validation_error 3 times
2. **Observation**: P2 pilot notes timestamp 14:23 - Quote: "I don't know if it worked, didn't hear anything"
3. **Screenshot**: before-sr-error.png shows error message has no role="alert" or aria-live
4. **WCAG**: 3.3.1 Error Identification (Level A) violation - errors not programmatically announced
5. **Prioritisation**: findings-table.csv row 1 - Priority score 7 (Impact 5 + Inclusion 5 - Effort 3)
6. **Fix**: implementation-diffs.md Fix #1 - Added role="alert" + aria-live="assertive" to error span
7. **Verification**: verification.csv Part A row F2 - 3.3.1 now PASS (tested with NVDA)
8. **Before/after**: verification.csv Part B - SR error detection improved from 0% to 100%
9. **Re-pilot**: P5 (SR user) pilot notes - "Heard error announcement immediately, corrected and succeeded"

**Complete chain**: Data → Observation → Interpretation → Fix → Verification ✅



---

## Submission Checklist

Before submitting, verify:

**Files**:
- [ ] This completed template (submission-template.md)
- [ ] metrics.csv (or pasted into Section 3)
- [ ] Pilot notes (P1-notes.md, P2-notes.md, etc. OR summarised in Section 6)
- [ ] Screenshots folder (all images referenced above)
- [ ] Privacy statement signed (top of document)

**Evidence chains**:
- [ ] findings-table.csv links to metrics.csv lines AND/OR pilot notes timestamps
- [ ] implementation-diffs.md references findings from table
- [ ] verification.csv Part B shows before/after for fixes

**Quality**:
- [ ] No PII in screenshots (checked twice!)
- [ ] Session IDs anonymous (P1_xxxx format, not real names)
- [ ] Honest reporting (limitations acknowledged if metrics didn't improve)
- [ ] WCAG criteria cited specifically (e.g., "3.3.1" not just "accessibility")

**Pass criteria met**:
- [ ] n=2+ participants (metrics.csv has 2+ session IDs)
- [ ] 3+ findings with evidence (findings-table.csv complete)
- [ ] 1-3 fixes implemented (implementation-diffs.md shows code)
- [ ] Regression complete (verification.csv has 20 checks)
- [ ] Before/after shown (verification.csv Part B has data)

---

**Ready to submit?** Upload this file + evidence folder to Gradescope by end of Week 10.

**Estimated completion time**: 12-15 hours across Weeks 9-10

**Good luck!** 🎯
