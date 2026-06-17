# Design Spec: Ethernet Auto-Toggle Beautification
Date: 2026-06-08

## Goal
Improve the aesthetics of the application following a "Clean & Minimalist" visual style. Focus on layout, padding, visual hierarchy, and modern Material Design 3 principles.

## Visual Style: Clean & Minimalist
- **Colors:** Neutral palette (Whites, Off-whites, Grays) with a single functional accent (e.g., Green for active state, Blue for primary actions).
- **Typography:** High legibility, varying weights for hierarchy.
- **Elevation:** Subtle shadows (0-4dp) to create depth without clutter.
- **Padding:** Generous whitespace (16dp-32dp) to prevent overcrowding.

## Main Screen Redesign (activity_main.xml)
- **Container:** Centered vertical layout with `32dp` padding.
- **Title:** "Ethernet Auto-Toggle" at the top, centered, using `headlineMedium` (Material 3).
- **Primary Component:** A `MaterialCardView` containing:
    - Service Status indicator ("ENABLED" / "DISABLED") with appropriate color coding.
    - `SwitchMaterial` for the master toggle.
    - Spacing: `24dp` internal padding.
- **Instruction Section:** "How it works" section below the card with:
    - List of steps for clarity.
    - Subtle iconography or bullets.
    - Lower opacity for secondary text.

## Setup Wizard Redesign (activity_setup.xml)
- **Consistency:** Use the same typography and padding rules.
- **Step Indicator:** Improved visibility of the current step.
- **Animations:** Subtle transitions between steps (if possible within scope).

## Implementation Details
- **Theme:** Update `themes.xml` to use Material 3 colors and styles.
- **Resources:** Create `colors.xml` and `dimens.xml` for reusable design tokens.
- **Components:** Utilize `MaterialCardView`, `TextView` (with style attributes), and `SwitchMaterial`.

## Acceptance Criteria
- App looks modern and professional.
- Layout is centered and balanced.
- Padding is consistent across all views.
- Contrast ratios meet accessibility standards (WCAG 2.1).
