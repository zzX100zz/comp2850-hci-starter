# Pilot Session: P2 (Keyboard User - Accessibility Test)
**Date**: 2025-11-27
**Metric Session ID**: P2_Keyboard
**Device**: Laptop, **No Mouse** (Keyboard Navigation Only)

## Pre-Test
- **09:43:00**: Read consent script.
- **09:43:20**: Participant agreed. "Yes."

## Task 1: Add Task
- **09:44:10**: Used `Tab` key to navigate. Focus ring (blue outline) was visible on input field.
- **09:44:25**: Typed "Buy milk".
- **09:44:27**: Pressed `Enter` to submit.
- **Metric Match**: `2025-11-27T09:44:27...` (Success, 12ms)
- **CRITICAL OBSERVATION**:
    - The task was added successfully to the list.
    - **User Comment**: "The keyboard operation itself is fine, the focus is clear. But I didn't hear or see a strong confirmation."
    - **User Insight**: "Since I can see, I know it worked. But if I were a blind user relying on a screen reader, I would have no idea because there was no alert or feedback." (Supports Finding: Success status silent for SR).

## Task 3: Delete Task
- **09:44:35**: Tabbed through the list to find the "Delete" button.
- **09:44:41**: Pressed `Enter` on Delete.
- **Metric Match**: `2025-11-27T09:44:41...` (Success, 2ms)
- **Observation**: Focus management worked reasonably well, but again, lack of feedback was noted.

## Debrief
- Functional for keyboard users, but fails WCAG criteria for Screen Reader feedback (Status Messages).
