# Factify App

A polished, modern, feature-rich Android application built with native **Java** and **XML**, designed with a premium **Material 3** dark theme and consistent **Montserrat** typography.

---

## 🌟 Overview

**[Factify](https://play.google.com/store/apps/details?id=com.factifyft.app)** is an engaging educational and entertainment application combining fascinating daily facts across multiple psychology and general knowledge categories with classic retro minigames (Quiz Show, Snake Game, and Tic Tac Toe).

The app has been completely designed and modernized to provide a sleek, responsive, and delightful user experience across all Android devices while strictly maintaining a lightweight, performant Java + XML architecture.

---

## 🚀 Key Features

### 1. 🧠 Daily Facts & Categories
- **Daily Cards:** Interactive front/back flip cards displaying curated daily facts with copy and share capabilities.
- **Categorized Knowledge:** Explore facts across multiple domains including *Interesting Facts*, *Psychology*, *Unknown Facts*, and *Hidden Truths*.
- **Favorites:** Save your favorite facts for quick offline access.

### 2. 🎮 Mini Games & Challenges
- **Quiz Show:** Test your knowledge with interactive multiple-choice trivia across Science, General Knowledge, Sports, and Technology.
- **Classic Snake Game:** Enjoy smooth retro gameplay with custom grid rendering and high-score tracking.
- **Tic Tac Toe:** Play Player vs Player or Player vs Computer with clean animations and polished dialogs.

### 3. 👤 Profile & Customization
- Customizable user profile name with live editing.
- Access to Privacy Policy, Terms of Service, and "More Apps to Try".

---

## 🎨 UI/UX Redesign Highlights

- **Material 3 DayNight Theme:** Premium dark palette featuring rich deep-space backgrounds (`#0F172A`), elevated surface cards (`#1E293B`), and vibrant accent gradients (Indigo, Emerald, Amber).
- **Consistent Typography:** Full enforcement of the **Montserrat** font family (`@font/montserrat`) across every text view, header, button, and card.
- **Modern Cards & Dialogs:** Elevated card views with subtle borders, rounded corners (20dp–32dp), and polished popup dialogs.
- **Policy-Compliant Ads:** Seamless integration of Google AdMob banner and native ads adhering strictly to Google Play guidelines.

---

## 🛠️ Tech Stack & Architecture

- **App Name:** [Factify](https://play.google.com/store/apps/details?id=com.factifyft.app) (`com.factifyft.app`)
- **Language:** 100% Java
- **UI Toolkit:** Android Views & XML Layouts with Material Components (M3)
- **Architecture:** Activity & Fragment-based modular navigation with ViewBinding / findViewById
- **Min SDK:** API 24 (Android 7.0+)
- **Compile/Target SDK:** API 36 / 37
- **Key Libraries:**
  - AndroidX AppCompat, ConstraintLayout, RecyclerView, CardView, GridLayout
  - Google Play Services Ads (AdMob)
  - AndroidX Core Splashscreen
  - Lifecycle Process

---

## 📁 Project Structure

```text
app/
├── src/main/
│   ├── java/com/factifyft/app/
│   │   ├── activities/      # Dashboard, Quiz, Games, Facts Display
│   │   ├── adapters/        # RecyclerView Adapters for categories & favorites
│   │   ├── data/            # Data models (FactCategory, etc.)
│   │   ├── fragments/       # Daily, Facts, Favorite, Games, Profile fragments
│   │   └── utils/           # AdManager, GameView, NetworkUtils, customToast
│   ├── res/
│   │   ├── drawable/        # Gradients, custom shapes, icons
│   │   ├── font/            # Montserrat font family (.ttf files & xml)
│   │   ├── layout/          # Redesigned XML activity and fragment layouts
│   │   ├── values/          # Colors, themes, strings, facts & quiz arrays
│   │   └── xml/             # Backup rules, network security config
│   ├── AndroidManifest.xml
│   └── build.gradle.kts
```

---

## ⚙️ Build & Setup Instructions

1. **Prerequisites:**
   - Android Studio (Koala / Ladybug or newer)
   - JDK 17
   - Android SDK Platform 36/37

2. **Open Project:**
   - Open Android Studio and select **Open**, then choose the project root directory.

3. **Build the App:**
   - Run Gradle sync.
   - Run `./gradlew assembleDebug` or click **Run 'app'** in Android Studio.

---

## 💪 Play Store link

 - Factify App : [Factify](https://play.google.com/store/apps/details?id=com.factifyft.app)
