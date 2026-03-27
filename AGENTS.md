# AMSS Project Context

## Overview
AMSS is an Android application developed by Pegasone. It is a health assessment tool designed to calculate the Acute Mountain Sickness (AMS) score through three sections: Self-report, Clinical, and Functional.

## Architecture & UI
- **Language:** Kotlin
- **Package Name:** `com.pegasone.AMSS`
- **Main Entry Point:** `MainActivity` serves as the primary container.
- **Navigation:** Uses a `TabLayout` with `ViewPager2` to switch between three core sections. Swipe navigation is disabled.
- **Global State Management:** 
    - `AMSSApplication` manages a color gradient and a dynamic `backgroundColor`.
    - `backgroundColor` is updated based on the **SELF-REPORT** total score (0-12).
    - State is exposed via `LiveData` and observed by the Activity and all Fragments.
- **UI Frameworks:**
    - **Material Components:** Used for buttons, tabs, and styling.
    - **View Binding:** Used for safe interaction with XML layouts.
- **Styling:**
    - **Earthy Theme:** Backgrounds use a dynamic gradient from `dark_green` to `red`. 
    - **Color Consistency:** The app maintains a "dark" earthy look regardless of the system's light or dark mode. 
    - **Text:** All text (including `RadioButton` labels, `AlertDialog` titles/messages, and buttons) is forced to `antique_white`.
    - **RadioButtons:** Globally styled to use `antique_white` for both text and the selection circle (`buttonTint`).
    - **Dialogs:** `AlertDialog`s are explicitly themed to use a dark surface (`very_dark_grey`) with `antique_white` titles and action buttons. This overrides system-wide Light mode defaults to maintain high contrast.
- **Edge-to-Edge:**
    - Compliant with Android 15+ requirements.
    - Uses `enableEdgeToEdge()` and handles window insets for the `AppBarLayout`.
    - Deprecated `statusBarColor` APIs have been removed.

## Features
- **Scoring:** 
    - Self-report (0-12), Clinical (0-10), Functional (0-3).
    - Dynamic labels: "No AMS", "Mild AMS", "Moderate AMS", "Severe AMS" based on self-report score.
- **Persistence:** 
    - All radio button selections are automatically saved to `SharedPreferences` on every tap.
    - Data is restored automatically when the app launches or resumes.
- **Reset Functionality:** 
    - A "Reset forms" button on the Self-Report view clears all three sections after confirmation.
    - Reset is handled globally via `AMSSApplication.triggerReset()`, which clears all preference files and notifies active fragments to refresh their UI.

## Localization
- Support for 34+ languages including all 24 official EU languages, Arabic, Hindi, Mandarin, Japanese, Urdu, Farsi, Tagalog, Swahili, Yoruba, Igbo, and Fula.
- RTL (Right-to-Left) support for Arabic, Urdu, and Farsi.
- Per-app language selection enabled in system settings.

## Project Structure
- `com.pegasone.AMSS`: Base package and `Application` class.
- `com.pegasone.AMSS.ui`: Organized by feature: `clinical`, `functional`, `selfreport`.

## Technical Stack
- **Build System:** Gradle (Kotlin DSL)
- **Minimum SDK:** Android 26
- **Target SDK:** 36
- **Key Dependencies:** `appcompat`, `viewpager2`, `material`, `core-ktx`, `lifecycle`, `navigation`, `activity-ktx`.
