# App Redesign and Modernization Plan

Complete redesign and modernization of the Java + XML Android app (`Learnly_skillbit_app`) to deliver a clean, modern, premium UI with consistent Montserrat typography, optimized layouts, cards, buttons, dialogs, and navigation, while strictly preserving all existing features and functionality.

## User Review Required

> [!IMPORTANT]
> - **Architecture Preserved:** The app remains 100% Java + XML (no migration to Kotlin/Compose).
> - **SDK & Dependencies:** Updated to target latest stable Android SDK (SDK 36/37) with modern AndroidX libraries.
> - **Typography:** Consistent application of Montserrat font across all views and text styles.
> - **Ad & Policy Compliance:** Ensuring AdMob and ad placement comply fully with Google Play policies.

## Open Questions

None. The scope is well-defined as a frontend UI/UX redesign and modernization keeping the Java + XML architecture intact.

## Proposed Changes

### Gradle & Configuration
- **[MODIFY]** [build.gradle.kts](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/build.gradle.kts): Ensure robust compilation settings, SDK 36/37 alignment, and up-to-date dependencies.
- **[MODIFY]** [AndroidManifest.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/../AndroidManifest.xml): Verify minimum required permissions and Google Play compliance.

### Theme & Typography
- **[MODIFY]** [themes.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/values/themes.xml): Ensure `android:fontFamily` and `fontFamily` point to Montserrat (`@font/montserrat`), Material 3 surface colors, card elevations, and shape themes.

### XML Layouts & UI Screens
Redesign and modernize all layouts for responsiveness, modern spacing, glassmorphism/gradient cards, pill buttons, and polished UX:
- **DashBoard & Fragments:**
  - [MODIFY] [activity_dashboard.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/activity_dashboard.xml) & bottom navigation
  - [MODIFY] [fragment_daily.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/fragment_daily.xml)
  - [MODIFY] [fragment_facts.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/fragment_facts.xml)
  - [MODIFY] [fragment_favorite.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/fragment_favorite.xml)
  - [MODIFY] [fragment_games.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/fragment_games.xml)
  - [MODIFY] [fragment_profile.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/fragment_profile.xml)
- **Activities & Game Screens:**
  - [MODIFY] [activity_quiz_type_select.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/activity_quiz_type_select.xml)
  - [MODIFY] [activity_quiz_show.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/activity_quiz_show.xml)
  - [MODIFY] [activity_tic_toc_toe_game_screen.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/activity_tic_toc_toe_game_screen.xml)
  - [MODIFY] [activity_snake_game_show.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/activity_snake_game_show.xml)
  - [MODIFY] [activity_facts_display.xml](file:///C:/Users/karth/AndroidStudioProjects/learnly_skillbit_app/app/src/main/res/layout/activity_facts_display.xml)
- **Cards & Dialogs:**
  - [MODIFY] Daily cards (`daily_card_front_side_view.xml`, `daily_card_back_side_view.xml`)
  - [MODIFY] Dialogs (`dialog_quiz_complete.xml`, `dialog_snake_game_over.xml`, `dialog_tic_game_over.xml`, `dialog_unlock_quiz.xml`)
  - [MODIFY] Item designs (`fact_category_design.xml`, `fav_item_design.xml`, `displayfacts_design.xml`, `native_ad_layout.xml`)

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to verify compilation and resource validity.
- Run unit/instrumentation tests if available.

### Manual Verification
- Review layout previews / device deployment to verify UI responsiveness, spacing, and typography across screens.
