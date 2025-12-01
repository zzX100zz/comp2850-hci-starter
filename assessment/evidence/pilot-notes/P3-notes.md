# Pilot Session: P3 (Mouse User - Robustness Test)
**Date**: 2025-11-27
**Metric Session ID**: P3_Mouse
**Device**: Laptop with Mouse

## Pre-Test
- **09:45:00**: Read consent script.
- **09:45:15**: Participant agreed.

## Task 1: Add Task
- **09:46:00**: Typed "Buy milk".
- **09:46:05**: Clicked "Add Task" button.
- **Metric Match 1**: `2025-11-27T09:46:05...` (Success)
- **CRITICAL OBSERVATION**:
    - After clicking, the user didn't see a prominent success message or visual change immediately.
    - User hesitated and clicked **AGAIN** just 1.4 seconds later.
    - **Quote**: "I didn't think it clicked the first time, nothing happened."
- **09:46:06**: Second click registered.
- **Metric Match 2**: `2025-11-27T09:46:06...` (Success - Duplicate created)
- **Outcome**: Two "Buy milk" tasks appeared in the list.
- **User Comment**: "Oh, I added it twice. There was no feedback so I thought it failed."

## Task 3: Delete Task (Cleanup)
- **09:46:12**: User noticed the duplicates and proceeded to delete them.
- **09:46:16**: Deleted first duplicate.
- **Metric Match**: `2025-11-27T09:46:16...`
- **09:46:18**: Deleted second duplicate.
- **Metric Match**: `2025-11-27T09:46:18...`

## Debrief
- System lacks "Visibility of System Status" (Nielsen Heuristic #1). Needs visual/audio confirmation or button disable state to prevent double submission.
