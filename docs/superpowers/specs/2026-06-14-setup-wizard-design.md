# Design Spec: Modern Dark Design System & Setup Wizard

## 1. Overview
This document defines the "Modern Dark" design system for Force Ethernet and the guided Setup Wizard flow. The goal is a consistent, professional "pro-tool" aesthetic across the entire application.

## 2. Global Design System: Modern Dark
To ensure UX consistency, these rules apply to all activities (`SetupActivity`, `MainActivity`, etc.):

### A. Color Palette (Tokens)
- **`background`**: Pure Black (#000000) - Used for the main window background.
- **`surface`**: Dark Gray (#1C1C1E) - Used for cards, containers, and elevated elements.
- **`primary`**: Vibrant Blue (#0A84FF) - Used for call-to-action buttons, progress bars, and "glow" effects.
- **`text_primary`**: White (#FFFFFF) - Used for headlines and main content.
- **`text_secondary`**: Muted Gray (#8E8E93) - Used for descriptions and de-emphasized text.
- **`accent_glow`**: Transparent Blue (rgba(10, 132, 255, 0.5)) - Used for soft shadows under active elements.

### B. UI Components
- **Corner Radius**: 16dp minimum for small buttons, 24dp for cards and large action buttons.
- **Elevation**: Minimal. Depth is created through color contrast (`surface` on `background`) rather than heavy shadows.
- **Typography**: San Francisco (on iOS-inspired designs) or Roboto (standard Android). Bold headlines, high line-height for body text.

## 3. Component: Setup Wizard
A structured flow managed by a state machine in `SetupActivity`.

### A. Steps
1. **Welcome**: Introduction.
2. **Notifications (Step 1/4)**: `POST_NOTIFICATIONS` permission.
3. **Accessibility (Step 2/4)**: Critical service for automation.
4. **Battery (Step 3/4)**: Whitelisting for background stability.
5. **Completion (Step 4/4)**: Success confirmation.

## 4. Component: Main Dashboard (MainActivity)
The main screen must be updated to match the design system:
- **Master Toggle**: A large, rounded switch or card with a blue glow when active.
- **Status Cards**: Surface-colored cards on the black background.
- **Consistency**: Icons and progress indicators must match the wizard's style.

## 5. Implementation Strategy
1. **Update `colors.xml`**: Replace existing light-mode colors with Modern Dark tokens.
2. **Update `themes.xml`**: Ensure the app uses a `DayNight` theme that defaults to the dark palette.
3. **Refactor `SetupActivity`**: Implement the 5-step flow with the new UI.
4. **Refactor `MainActivity`**: Apply the design system tokens to the dashboard layout.
